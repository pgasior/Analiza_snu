package pl.gasior.analizasnu.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

import pl.gasior.analizasnu.EventBusPOJO.CalibrationEvent;
import pl.gasior.analizasnu.EventBusPOJO.EventTimeElapsed;
import pl.gasior.analizasnu.EventBusPOJO.RecordingFinishedEvent;
import pl.gasior.analizasnu.R;
import pl.gasior.analizasnu.RecordService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordFragmentAlt.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordFragmentAlt#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragmentAlt extends Fragment {

    private OnFragmentInteractionListener mListener;

    enum RecordFragmentStates {BEFORE_CALIBRATION,CALIBRATING,RECORDING};

    RecordFragmentStates currentState;

    TextView tv;
    boolean recording;
    Button startRecord;
    Button stopRecord;
    TextView tvBackgroundLevel;
    TextView tvCalibrationLevel;
    Button calibrationButton;
    Button endCalibrationButton;
    CalibrationRetainingFragment calibrationRetainingFragment;
    double calibrationLevel;
    boolean calibrationDone;
    CheckBox cbAnalysisDuringRecording;
    TextView tvUserInstruction;

    public RecordFragmentAlt() {
        // Required empty public constructor

    }


    // TODO: Rename and change types and number of parameters
    public static RecordFragmentAlt newInstance() {
        RecordFragmentAlt fragment = new RecordFragmentAlt();

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
        View view = inflater.inflate(R.layout.fragment_record_alt, container, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        calibrationRetainingFragment = (CalibrationRetainingFragment)fm.findFragmentByTag("calibrationRetainingFragment");
        if(calibrationRetainingFragment == null) {
            calibrationRetainingFragment = new CalibrationRetainingFragment();
            fm.beginTransaction().add(calibrationRetainingFragment,"calibrationRetainingFragment").commit();
        }
        tvUserInstruction = (TextView)view.findViewById(R.id.tvUserInstruction);
        cbAnalysisDuringRecording = (CheckBox)view.findViewById(R.id.cbAnalysisDuringRecording);
        startRecord = (Button)view.findViewById(R.id.startRecordButton);
//        startRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recording = true;
//                Intent intent = new Intent(getActivity(),RecordService.class);
//                intent.putExtra("calibrationLevel", calibrationLevel);
//                intent.putExtra("removeSilence",cbAnalysisDuringRecording.isChecked());
//                getActivity().startService(intent);
//                updateState();
//            }
//        });

        stopRecord = (Button)view.findViewById(R.id.stopRecordButton);
//        stopRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recording = false;
//                Intent intent = new Intent(getActivity(),RecordService.class);
//                getActivity().stopService(intent);
//                currentState = RecordFragmentStates.BEFORE_CALIBRATION;
//                updateState();
//
//            }
//        });
        tv = (TextView)view.findViewById(R.id.timeTextView);
        tvBackgroundLevel = (TextView)view.findViewById(R.id.tvBackgroundLevel);
        tvCalibrationLevel = (TextView)view.findViewById(R.id.tvCalibrationLevel);
        calibrationButton = (Button)view.findViewById(R.id.calibrationButton);
        calibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibrationRetainingFragment.startCalibration();
                calibrationDone=false;
                updateState();
            }
        });
        endCalibrationButton = (Button)view.findViewById(R.id.endCalibrationButton);
//        endCalibrationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                calibrationDone=true;
//                calibrationRetainingFragment.stopCalibration();
//                //todo odczyt z konfiguracji
//                calibrationLevel+=2.0;
//                DecimalFormat df=new DecimalFormat("0.00");
//                String format = df.format(calibrationLevel);
//                tvCalibrationLevel.setText(String.valueOf(format));
//                updateState();
//            }
//        });
        currentState = RecordFragmentStates.BEFORE_CALIBRATION;
        if(savedInstanceState!=null) {
            tv.setText(savedInstanceState.getString("timeElapsed"));
            recording=savedInstanceState.getBoolean("recording");
            calibrationLevel = savedInstanceState.getDouble("calibrationLevel");
            calibrationDone = savedInstanceState.getBoolean("calibrationDone");
            tvCalibrationLevel.setText(String.valueOf(calibrationLevel));
            cbAnalysisDuringRecording.setChecked(savedInstanceState.getBoolean("cbAnalysisDuringRecording"));
            currentState = (RecordFragmentStates)savedInstanceState.get("currentState");
        }
        updateState();
        return view;
    }

    private void updateState() {
        switch(currentState) {
            case BEFORE_CALIBRATION:
                tvUserInstruction.setVisibility(View.INVISIBLE);
                startRecord.setText("Rozpocznij kalibrację");
                startRecord.setEnabled(true);
                startRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)getActivity()).disableDrawer();
                        currentState = RecordFragmentStates.CALIBRATING;
                        calibrationRetainingFragment.startCalibration();
                        calibrationDone=false;
                        updateState();
                    }
                });
                stopRecord.setEnabled(false);
                break;
            case CALIBRATING:
                tvUserInstruction.setVisibility(View.VISIBLE);
                stopRecord.setEnabled(true);

                stopRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calibrationDone = true;
                        calibrationRetainingFragment.stopCalibration();
                        currentState = RecordFragmentStates.BEFORE_CALIBRATION;
                        ((MainActivity)getActivity()).enableDrawer();
                        updateState();
                    }
                });
                startRecord.setText("Start");
                startRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calibrationDone = true;
                        calibrationRetainingFragment.stopCalibration();
                        //todo odczyt z konfiguracji
                        calibrationLevel += 2.0;
                        DecimalFormat df = new DecimalFormat("0.00");
                        String format = df.format(calibrationLevel);
                        tvCalibrationLevel.setText(String.valueOf(format));
                        //updateState();
                        recording = true;
                        Intent intent = new Intent(getActivity(), RecordService.class);
                        intent.putExtra("calibrationLevel", calibrationLevel);
                        intent.putExtra("removeSilence", cbAnalysisDuringRecording.isChecked());
                        getActivity().startService(intent);
                        currentState = RecordFragmentStates.RECORDING;
                        updateState();
                    }
                });
                break;
            case RECORDING:
                tvUserInstruction.setVisibility(View.INVISIBLE);
                startRecord.setEnabled(false);
                stopRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recording = false;
                        Intent intent = new Intent(getActivity(),RecordService.class);
                        getActivity().stopService(intent);
                        ((MainActivity)getActivity()).enableDrawer();
                        currentState = RecordFragmentStates.BEFORE_CALIBRATION;
                        updateState();
                    }
                });
                break;
        }
//        if(recording) {
//            startRecord.setEnabled(false);
//            stopRecord.setEnabled(true);
//            calibrationButton.setEnabled(false);
//            endCalibrationButton.setEnabled(false);
//            ((MainActivity)getActivity()).disableDrawer();
//        } else {
//            startRecord.setEnabled(false);
//            stopRecord.setEnabled(false);
//            calibrationButton.setEnabled(true);
//            endCalibrationButton.setEnabled(false);
//            if(calibrationRetainingFragment.isCalibrating()) {
//                startRecord.setEnabled(false);
//                stopRecord.setEnabled(false);
//                calibrationButton.setEnabled(false);
//                endCalibrationButton.setEnabled(true);
//            }
//            if(calibrationDone) {
//                startRecord.setEnabled(true);
//                endCalibrationButton.setEnabled(false);
//                calibrationButton.setEnabled(true);
//            }
////            else {
////                startRecord.setEnabled(false);
////            }
////            stopRecord.setEnabled(false);
////            calibrationButton.setEnabled(true);
//            ((MainActivity)getActivity()).enableDrawer();
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("timeElapsed",tv.getText().toString());
        outState.putBoolean("recording", recording);
        outState.putDouble("calibrationLevel",calibrationLevel);
        outState.putBoolean("calibrationDone",calibrationDone);
        outState.putBoolean("cbAnalysisDuringRecording",cbAnalysisDuringRecording.isChecked());
        outState.putSerializable("currentState", currentState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void calibrationEventHandler(CalibrationEvent ev) {
        calibrationLevel = ev.getCalibrationValue();
        DecimalFormat df=new DecimalFormat("0.00");
        String format = df.format(calibrationLevel);
        tvBackgroundLevel.setText(String.valueOf(format));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recordFinishedHandler(RecordingFinishedEvent ev) {
        Intent intent = new Intent(getActivity(),DreamMetadataEditActivity.class);
        intent.putExtra("filename",ev.getFilename());
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateTime(EventTimeElapsed ev) {
        int totalSecs = ev.getTime();
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        tv.setText(timeString);
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
        if(calibrationRetainingFragment.isCalibrating()) {
            calibrationRetainingFragment.stopCalibration();
        }
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
