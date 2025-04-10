package com.example.selfalarm.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfalarm.R;
import com.example.selfalarm.adapter.ContactAdapter;
import com.example.selfalarm.dao.ContactDao;
import com.example.selfalarm.model.Contact;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {
    private RecyclerView contactsList;
    private TextInputEditText searchInput;
    private ContactAdapter contactAdapter;
    private ContactDao contactDao;
    private List<Contact> contacts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_phone_tab, container, false);

        contactDao = new ContactDao(requireContext());

        // Khởi tạo views
        contactsList = view.findViewById(R.id.contacts_list);
        searchInput = view.findViewById(R.id.search_contacts);

        // Setup RecyclerView
        contactsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        contactAdapter = new ContactAdapter(requireContext(), contacts);
        contactsList.setAdapter(contactAdapter);

        // Setup tìm kiếm
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadContacts();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật danh sách khi quay lại fragment
        loadContacts();
    }

    private void loadContacts() {
        contacts = contactDao.getAllContacts();
        contactAdapter.updateData(contacts);
    }
}