package com.example.q.madcamp01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.example.q.madcamp01.R.id.swipeContainer;

/**
 * Created by q on 2016-12-26.
 */

public class ContactFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    ListViewAdapter mAdapter;


    private static final String ARG_SECTION_NUMBER = "section_number";

    Context context;

    public ContactFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ContactFragment newInstance(int sectionNumber) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @SuppressLint("NewApi")
    private int getSoftButtonsBarHeight() {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,
            },0);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            GetContactTask task = new GetContactTask();
            task.execute((Void[])null);
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        // Configure the refreshing colors

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                GetContactTask task = new GetContactTask();
                task.execute((Void[])null);
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private class ViewHolder
    {
        public TextView name;
        public TextView number;
        public Button callButton;
        public Button msgButton;

    }
    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData;

        public ListViewAdapter(Context mContext, ArrayList<ListData> lst) {
            super();
            this.mContext = mContext;
            this.mListData = lst;
        }

        public void addItem(String name, String number) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.name = name;
            addInfo.number = number;

            mListData.add(addInfo);
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.format, null);

                holder.name = (TextView) convertView.findViewById(R.id.textView1);
                holder.number = (TextView) convertView.findViewById(R.id.textView2);
                holder.callButton = (Button) convertView.findViewById(R.id.button1);
                holder.msgButton = (Button) convertView.findViewById(R.id.button2);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData mData = mListData.get(position);


            holder.name.setText(mData.name);
            holder.number.setText(mData.number);

            holder.callButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // Send single item click data to SingleItemView Class
                    String phoneNumber = mListData.get(position).number;
                    if (!phoneNumber.equals("none")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                        getActivity().startActivity(intent);
                        //try startActivityForResult
                    }
                }
            });

            holder.msgButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // Send single item click data to SingleItemView Class
                    String phoneNumber = mListData.get(position).number;
                    if (!phoneNumber.equals("none")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);

                        intent.setData(Uri.parse("smsto:"));
                        intent.setType("vnd.android-dir/mms-sms");
                        intent.putExtra("address", phoneNumber);
                        try {
                            mContext.startActivity(intent);
                        } catch(android.content.ActivityNotFoundException e) {
                            Toast.makeText(mContext, "문자 메시지 전송 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            return convertView;
        }
        public void GetUserContactsList() {
            GetContactTask task = new GetContactTask();
            task.execute((Void[])null);
        }
    }

    public class GetContactTask extends AsyncTask<Void, String, Void> {

        ListView lv;
        ArrayList<ListData> ld_list;
        ListViewAdapter m_adapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ld_list = new ArrayList<>();
            lv = (ListView) getActivity().findViewById(R.id.listView);
            lv.setPadding(0,0,0,getSoftButtonsBarHeight());
            m_adapter = new ListViewAdapter(getActivity(), ld_list);
            lv.setAdapter(m_adapter);
        }

        @Override
        protected Void doInBackground(Void... v) {
            String[] arrProjection = {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
            };
            String[] arrPhoneProjection = {
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            // get user list
            Cursor clsCursor = getActivity().getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, arrProjection,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                    null, null
            );

            while (clsCursor.moveToNext()) {
                String strContactId = clsCursor.getString(0);
                // phone number
                Cursor clsPhoneCursor = getActivity().getContentResolver().query (
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrPhoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + strContactId,
                        null, null
                );

                while( clsPhoneCursor.moveToNext() ) {
                    // add name, number
                    publishProgress(clsCursor.getString(1), clsPhoneCursor.getString(0));
                }
                clsPhoneCursor.close();

            }
            clsCursor.close();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... stringArgs) {
            // 파일 다운로드 퍼센티지 표시 작업
            m_adapter.addItem(stringArgs[0], stringArgs[1]);
            m_adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void v) {
            // doInBackground 에서 받아온 total 값 사용 장소
            lv.setOnItemClickListener
                    (
                            new AdapterView.OnItemClickListener()
                            {
                                public void onItemClick(AdapterView parent, View view, int position, long id)
                                {
                                    String str = mAdapter.mListData.get(position).name;
                                    String a = str + " 선택";
                                    Toast.makeText(getActivity(), a, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
        }
    }
}

