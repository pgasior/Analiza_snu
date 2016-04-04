package pl.gasior.analizasnu.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import pl.gasior.analizasnu.R;
import pl.gasior.analizasnu.db.DreamListContract;
import pl.gasior.analizasnu.db.DreamListProvider;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListenRecordingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListenRecordingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListenRecordingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnFragmentInteractionListener mListener;
    SimpleCursorAdapter adapter;
    ListView lv;

    public ListenRecordingFragment() {
        // Required empty public constructor
    }


    public static ListenRecordingFragment newInstance() {
        ListenRecordingFragment fragment = new ListenRecordingFragment();

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
        View view = inflater.inflate(R.layout.fragment_listen_recording, container, false);
        lv = (ListView)view.findViewById(R.id.listView);

        String[] fromColumns = {DreamListContract.DreamEntry.COLUMN_NAME_AUDIO_FILENAME};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1
        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,null,
                fromColumns,toViews,0);
        lv.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);

        return view;
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
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("RF", "oncreateloader");
        return new CursorLoader(getActivity().getApplicationContext(),
                DreamListProvider.CONTENT_URI,
                new String[]{DreamListContract.DreamEntry._ID,DreamListContract.DreamEntry.COLUMN_NAME_AUDIO_FILENAME},
                null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
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
