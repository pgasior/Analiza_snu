package pl.gasior.analizasnu.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import pl.gasior.analizasnu.R;


/**
 * Created by Piotrek on 20.05.2016.
 */
public class ConfirmAnalysisDialogFragment extends DialogFragment {

    public interface ConfirmAnalysisDialogListener {
        public void onConfirmAnalysisDialogPositiveClick();
        public void onConfirmAnalysisDialogNegativeClick();
    }
    ConfirmAnalysisDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConfirmAnalysisDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.analysis_information))
                .setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onConfirmAnalysisDialogPositiveClick();
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onConfirmAnalysisDialogNegativeClick();
                    }
                });
        return builder.create();
    }
}
