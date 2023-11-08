package sdu.msd.ui.createGroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import sdu.msd.databinding.FragmentCreateGroupBinding;

public class createGroupFragment extends Fragment {
    private FragmentCreateGroupBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CreateGroupViewModel groupModel = new ViewModelProvider(this).get(CreateGroupViewModel.class);
        binding = FragmentCreateGroupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        groupModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
