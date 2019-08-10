import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.yessumtorah.boilerapp.R;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    ArrayAdapter<String> myArrayAdapter;
    ArrayList<String> myArrayList;


    public ListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        myArrayList = new ArrayList<>();


        return view;
    }

}
