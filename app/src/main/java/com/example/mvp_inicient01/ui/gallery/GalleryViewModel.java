package com.example.mvp_inicient01.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("gabrielbbmatozinhos@gmail.com" + "\n\n" +
                "agmarbento@cefetmg.br\n");
    }

    public LiveData<String> getText() {
        return mText;
    }
}