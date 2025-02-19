package com.example.mobilebankingapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {
    private List<Transaction> transactions;

    // Constructor
    public TransactionsAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        // Bind data to the views
        Transaction transaction = transactions.get(position);
        holder.billType.setText(transaction.getBillType());
        holder.accountNumber.setText(transaction.getAccountNumber());
        holder.amount.setText(transaction.getAmount());
        holder.date.setText(transaction.getDate());
        holder.time.setText(transaction.getTime());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    // ViewHolder class
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView billType, accountNumber, amount, date, time;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            billType = itemView.findViewById(R.id.billType);
            accountNumber = itemView.findViewById(R.id.accountNumber);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
        }
    }
}