package com.example.mvp_inicient01.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Esse presente trabalho foi\n" +
                "pensado para ...\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Fusce eu felis sapien. Sed vitae nulla vel risus varius sagittis eget eu diam." +
                " Proin nibh leo, blandit sed neque sit amet, tincidunt maximus tellus. Ut sed lectus urna." +
                " Sed pulvinar, dui quis luctus volutpat, dui erat volutpat neque." +
                "Esse presente trabalho foi\n" +
                "pensado para ...\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce eu felis sapien. Sed vitae nulla vel risus varius sagittis eget eu diam. Proin nibh leo, blandit sed neque sit amet, tincidunt maximus tellus. Ut sed lectus urna. Sed pulvinar, dui quis luctus volutpat, dui erat volutpat neque." +
                "Esse presente trabalho foi\n" +
                "pensado para ...\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce eu felis sapien. Sed vitae nulla vel risus varius sagittis eget eu diam. Proin nibh leo, blandit sed neque sit amet, tincidunt maximus tellus. Ut sed lectus urna. Sed pulvinar, dui quis luctus volutpat, dui erat volutpat neque." +
                "Esse presente trabalho foi\n" +
                "pensado para ...\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce eu felis sapien. Sed vitae nulla vel risus varius sagittis eget eu diam. Proin nibh leo, blandit sed neque sit amet, tincidunt maximus tellus. Ut sed lectus urna. Sed pulvinar, dui quis luctus volutpat, dui erat volutpat neque." +
                "Esse presente trabalho foi\n" +
                "pensado para ...\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce eu felis sapien. Sed vitae nulla vel risus varius sagittis eget eu diam. Proin nibh leo, blandit sed neque sit amet, tincidunt maximus tellus. Ut sed lectus urna. Sed pulvinar, dui quis luctus volutpat, dui erat volutpat neque." +
                "Esse presente trabalho foi\n" +
                "pensado para ...\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce eu felis sapien. Sed vitae nulla vel risus varius sagittis eget eu diam. Proin nibh leo, blandit sed neque sit amet, tincidunt maximus tellus. Ut sed lectus urna. Sed pulvinar, dui quis luctus volutpat, dui erat volutpat neque." +
                "");
    }

    public LiveData<String> getText() {
        return mText;
    }
}