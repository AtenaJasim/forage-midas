package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.incentive.IncentiveClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private final UserRepository userRepo;
    private final TransactionRepository txRepo;
    private final IncentiveClient incentiveClient;

    public TransactionService(UserRepository userRepo, TransactionRepository txRepo, IncentiveClient incentiveClient) {
        this.userRepo = userRepo;
        this.txRepo = txRepo;
        this.incentiveClient = incentiveClient;
    }

    @Transactional
    public void process(Transaction dto) {
        var senderOpt = userRepo.findById(dto.getSenderId());
        var recipientOpt = userRepo.findById(dto.getRecipientId());
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) return;

        var sender = senderOpt.get();
        var recipient = recipientOpt.get();

        float amount = dto.getAmount();
        if (sender.getBalance() < amount) return;

        float incentive = incentiveClient.fetchIncentive(dto);

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentive);

        userRepo.save(sender);
        userRepo.save(recipient);
        txRepo.save(new TransactionRecord(sender, recipient, amount, incentive));
    }
}
