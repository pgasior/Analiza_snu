package pl.gasior.analizasnu.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.File;

import pl.gasior.analizasnu.R;
import pl.gasior.analizasnu.db.DreamListContract;
import pl.gasior.analizasnu.db.DreamListDbHelper;
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
    private void startDetailActivity(String filename) {
        Intent intent = new Intent(getActivity(),DreamDetailActivity.class);
        intent.putExtra("filename", filename);
        startActivity(intent);
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
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                String filename = c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_AUDIO_FILENAME));
                startDetailActivity(filename);

            }
        });
        registerForContextMenu(lv);
        getLoaderManager().initLoader(0, null, this);

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater= getActivity().getMenuInflater();
        inflater.inflate(R.menu.dream_list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete:
                deleteDream(info.position);
            default:
                return super.onContextItemSelected(item);
        }

    }

    private void deleteDream(int position) {
        Cursor c = (Cursor)lv.getAdapter().getItem(position);
        int id = c.getInt(c.getColumnIndex(DreamListContract.DreamEntry._ID));
        String filename = c.getString(c.getColumnIndex(DreamListContract.DreamEntry.COLUMN_NAME_AUDIO_FILENAME));
        File f = new File( getActivity().getExternalFilesDir(null),filename);
        if(f.exists()) {
            f.delete();
        }
        DreamListDbHelper db = new DreamListDbHelper(getActivity());
        db.getWritableDatabase().delete(
                DreamListContract.DreamEntry.TABLE_NAME,
                DreamListContract.DreamEntry._ID+"="+id,
                null
        );
        db.close();
        getLoaderManager().restartLoader(0,null,this);
        //lv.getAdapter().notify();
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
        Uri uri = DreamListContract.BASE_CONTENT_URI.buildUpon().appendPath(DreamListContract.PATH_DREAMS).build();
        Log.i("RF",uri.toString());
        return new CursorLoader(getActivity().getApplicationContext(),
                uri,
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
