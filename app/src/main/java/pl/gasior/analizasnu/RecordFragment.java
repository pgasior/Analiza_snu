package pl.gasior.analizasnu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import pl.gasior.analizasnu.EventBusPOJO.EventTimeElapsed;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    TextView tv;
    boolean recording;
    Button startRecord;
    Button stopRecord;

    public RecordFragment() {
        // Required empty public constructor

    }


    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance() {
        RecordFragment fragment = new RecordFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        startRecord = (Button)view.findViewById(R.id.startRecordButton);
        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recording = true;
                Intent intent = new Intent(getActivity(),RecordService.class);
                getActivity().startService(intent);
                updateState();
            }
        });

        stopRecord = (Button)view.findViewById(R.id.stopRecordButton);
        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recording = false;
                Intent intent = new Intent(getActivity(),RecordService.class);
                getActivity().stopService(intent);
                updateState();
            }
        });
        tv = (TextView)view.findViewById(R.id.textView2);
        if(savedInstanceState!=null) {
            tv.setText(savedInstanceState.getString("timeElapsed"));
            recording=savedInstanceState.getBoolean("recording");
        }
        updateState();
        return view;
    }

    private void updateState() {
        if(recording) {
            startRecord.setEnabled(false);
            stopRecord.setEnabled(true);
            ((MainActivity)getActivity()).disableDrawer();
        } else {
            startRecord.setEnabled(true);
            stopRecord.setEnabled(false);
            ((MainActivity)getActivity()).enableDrawer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("timeElapsed",tv.getText().toString());
        outState.putBoolean("recording", recording);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateTime(EventTimeElapsed ev) {
        tv.setText(Integer.toString(ev.getTime()));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
