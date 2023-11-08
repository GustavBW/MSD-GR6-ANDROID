package sdu.msd.ui.createGroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateGroupViewModel extends ViewModel {
    private MutableLiveData<String> text = new MutableLiveData<>();

    public CreateGroupViewModel() {
        text.setValue("This is Fragment");
    }

    public LiveData<String> getText() {
        return text;
    }
}
