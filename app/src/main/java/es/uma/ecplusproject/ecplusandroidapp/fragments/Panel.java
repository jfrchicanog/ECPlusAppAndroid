package es.uma.ecplusproject.ecplusandroidapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public abstract class Panel extends Fragment {
    public static final String NAME = "name";
    public String getFragmentName() {
        return getArguments().getString(NAME);
    }
}
