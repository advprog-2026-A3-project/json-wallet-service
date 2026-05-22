package com.json.demo.grpc;

import com.json.demo.model.UserWallet;
import com.json.demo.service.WalletOperations;
import com.json.demo.service.transaction.PaymentMethod;
import com.json.demo.web.exception.InsufficientBalanceException;
import com.json.demo.web.exception.InvalidAmountException;
import com.json.demo.web.exception.WalletNotFoundException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import com.json.demo.service.transaction.PaymentMethod;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;

@GrpcService
@RequiredArgsConstructor
public class WalletGrpcEndpoint extends WalletGrpcServiceGrpc.WalletGrpcServiceImplBase {
    private final WalletOperations walletOperations;

    @Override
    public void getWalletByUserId(GetWalletRequest request, StreamObserver<WalletResponse> responseObserver) {
        execute(responseObserver, () -> walletOperations.getWalletByUserId(request.getUserId()));
    }

    @Override
    public void createWallet(CreateWalletRequest request, StreamObserver<WalletResponse> responseObserver) {
        BigDecimal initialBalance = request.getInitialBalance().isBlank()
                ? BigDecimal.ZERO
                : new BigDecimal(request.getInitialBalance());
        execute(responseObserver, () -> walletOperations.createWallet(request.getUserId(), initialBalance));
    }

    @Override
    public void topUp(WalletAmountRequest request, StreamObserver<WalletResponse> responseObserver) {
    execute(responseObserver, () -> walletOperations.topUp(
            request.getUserId(),
            new BigDecimal(request.getAmount()),
            PaymentMethod.BANK_TRANSFER  // default dulu
    ));
}

    @Override
    public void withdraw(WalletAmountRequest request, StreamObserver<WalletResponse> responseObserver) {
        execute(responseObserver, () -> walletOperations.withdraw(request.getUserId(), new BigDecimal(request.getAmount())));
    }

    private void execute(StreamObserver<WalletResponse> responseObserver, WalletSupplier supplier) {
        try {
            UserWallet wallet = supplier.get();
            responseObserver.onNext(toResponse(wallet));
            responseObserver.onCompleted();
        } catch (WalletNotFoundException ex) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException());
        } catch (InsufficientBalanceException | InvalidAmountException ex) {
            responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(ex.getMessage()).asRuntimeException());
        } catch (NumberFormatException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Amount must be numeric").asRuntimeException());
        } catch (Exception ex) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal wallet service error").asRuntimeException());
        }
    }

    private WalletResponse toResponse(UserWallet wallet) {
        return WalletResponse.newBuilder()
                .setId(wallet.getId() == null ? "" : wallet.getId().toString())
                .setUserId(wallet.getUserId())
                .setBalance(wallet.getBalance().toPlainString())
                .setCreatedAt(wallet.getCreatedAt() == null ? "" : wallet.getCreatedAt().toString())
                .setUpdatedAt(wallet.getUpdatedAt() == null ? "" : wallet.getUpdatedAt().toString())
                .build();
    }

    @FunctionalInterface
    private interface WalletSupplier {
        UserWallet get();
    }
}
