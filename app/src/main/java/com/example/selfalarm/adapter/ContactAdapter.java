package com.example.selfalarm.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfalarm.R;
import com.example.selfalarm.activity.phoneActivity.ContactDetailActivity;
import com.example.selfalarm.dao.CallLogDao;
import com.example.selfalarm.model.CallLog;
import com.example.selfalarm.model.Contact;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable {
    private Context context;
    private List<Contact> contactList;
    private List<Contact> contactListFull;
    private CallLogDao callLogDao;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.contactListFull = new ArrayList<>(contactList);
        this.callLogDao = new CallLogDao(context);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_phone_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        holder.contactName.setText(contact.getName());
        holder.contactPhone.setText(contact.getPhoneNumber());

        // Handle avatar (placeholder image)
        // Can be improved to load actual contact photo

        // Call button click
        holder.btnCall.setOnClickListener(v -> {
            String phoneNumber = contact.getPhoneNumber();
            // Add call log
            CallLog newLog = new CallLog(
                    phoneNumber,
                    contact.getName(),
                    System.currentTimeMillis(),
                    0, // Duration unknown yet
                    CallLog.CALL_TYPE_OUTGOING);
            callLogDao.addCallLog(newLog);

            // Initiate phone call
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
        });

        // Item click to view/edit contact details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ContactDetailActivity.class);
            intent.putExtra("contact_id", contact.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateData(List<Contact> newContactList) {
        this.contactList = newContactList;
        this.contactListFull = new ArrayList<>(newContactList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    private Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Contact> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(contactListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Contact contact : contactListFull) {
                    if (contact.getName().toLowerCase().contains(filterPattern) ||
                            contact.getPhoneNumber().contains(filterPattern)) {
                        filteredList.add(contact);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contactList.clear();
            contactList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView contactAvatar;
        TextView contactName;
        TextView contactPhone;
        ImageButton btnCall;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactAvatar = itemView.findViewById(R.id.contact_avatar);
            contactName = itemView.findViewById(R.id.contact_name);
            contactPhone = itemView.findViewById(R.id.contact_phone);
            btnCall = itemView.findViewById(R.id.btn_call);
        }
    }
}