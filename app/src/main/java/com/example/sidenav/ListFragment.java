package com.example.sidenav;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ListFragment extends Fragment {

    CardView cardProducts, cardCustomer, cardWallet; // Add cardWallet
    TextView tvProfileName, tvProfileEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment, container, false);

        // Find views
        cardProducts = view.findViewById(R.id.openProducts);
        cardCustomer = view.findViewById(R.id.Customer);
        cardWallet = view.findViewById(R.id.card_wallet); // Wallet card
        tvProfileName = view.findViewById(R.id.profile_name);
        tvProfileEmail = view.findViewById(R.id.profile_email);

        // Products card click
        cardProducts.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Products.class);
            intent.putExtra("customer_name", "John Doe");
            intent.putExtra("customer_phone", "123-456-7890");
            intent.putExtra("customer_email", "john.doe@example.com");
            startActivity(intent);
        });

        // Wallet card click
        cardWallet.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WalletActivity.class); // Create this activity
            startActivity(intent);
        });

        // Customer card click
        cardCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Customers.class); // Replace with actual activity if needed
            startActivity(intent);
        });

        // Get fullName and email from arguments
        Bundle args = getArguments();
        if (args != null) {
            String fullName = args.getString("fullName");
            String email = args.getString("email");

            if (fullName != null) tvProfileName.setText(fullName);
            if (email != null) tvProfileEmail.setText(email);
        }

        return view;
    }
}
