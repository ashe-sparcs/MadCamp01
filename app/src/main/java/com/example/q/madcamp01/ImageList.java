package com.example.q.madcamp01;


        import java.util.ArrayList;

        import android.Manifest;
        import android.app.Activity;
        import android.content.pm.PackageManager;
        import android.media.Image;
        import android.os.Build;
        import android.support.v4.app.Fragment;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.BaseAdapter;
        import android.widget.GridView;
        import android.widget.ImageView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.ListView;
        import android.widget.Toast;

public class ImageList extends Fragment {

    public Context mContext;
    GridView gv;
    ImageAdapter mAdapter;

    public static ImageList newInstance() {
        ImageList fragment = new ImageList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_2, container, false);
        gv = (GridView) rootView.findViewById(R.id.ImgGridView);


        /*GridView gv = (GridView)findViewById(R.id.ImgGridView);
        final ImageAdapter ia = new ImageAdapter(getActivity());
        gv.setAdapter(ia);*/


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ImageList.ImageAdapter(getActivity());
        gv.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            mAdapter.getThumbInfo(mAdapter.thumbsIDList, mAdapter.thumbsDataList);
        }

        gv.setOnItemClickListener(new OnItemClickListener() {
                                      public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                          mAdapter.callImageViewer(position);
                                      }
                                  });



        /*for(int a=0; a<mListData.length;a++)
        {
            mAdapter.addItem(mListData.getString(a));
        }


        mAdapter.addItem("김재성", "010-2908-8041");
        Log.d("onactivity created", "hey");*/


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 0);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            GetUserContactsList();
        }





        gv.setOnItemClickListener
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
                );*/
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                mAdapter.getThumbInfo(mAdapter.thumbsIDList, mAdapter.thumbsDataList);
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * ==========================================
     * Adapter class
     * ==========================================
     */
    public class ImageAdapter extends BaseAdapter {
        private String imgData;
        private String geoData;
        private ArrayList<String> thumbsDataList;
        private ArrayList<String> thumbsIDList;

        ImageAdapter(Context c) {
            mContext = c;
            thumbsDataList = new ArrayList<String>();
            thumbsIDList = new ArrayList<String>();
        }

        public final void callImageViewer(int selectedIndex){
            Intent i = new Intent(mContext, FullscreenActivity.class);
            //String imgPath = getImageInfo(imgData, geoData, thumbsIDList.get(selectedIndex));
            String imgPath = getImageInfo(null, null, thumbsIDList.get(selectedIndex));
            i.putExtra("filename", imgPath);
            i.putStringArrayListExtra("thumbsIDList", thumbsIDList);
            i.putExtra("selectedIndex", selectedIndex);
            startActivityForResult(i, 1);
        }

        public boolean deleteSelected(int sIndex) {
            return true;
        }

        public int getCount() {
            return thumbsIDList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(2, 2, 2, 2);
            } else {
                imageView = (ImageView) convertView;
            }
            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 8;
            Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(position), bo);
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 300, 300, true);
            imageView.setImageBitmap(resized);

            return imageView;
        }





        private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas) {
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};

            Cursor imageCursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);
            Log.d("ID" , "added");
            if (imageCursor != null && imageCursor.moveToFirst()) {
                String title;
                String thumbsID;
                String thumbsImageID;
                String thumbsData;
                String data;
                String imgSize;

                int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
                int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int thumbsSizeCol = imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                int num = 0;
                do {
                    thumbsID = imageCursor.getString(thumbsIDCol);
                    thumbsData = imageCursor.getString(thumbsDataCol);
                    thumbsImageID = imageCursor.getString(thumbsImageIDCol);
                    imgSize = imageCursor.getString(thumbsSizeCol);
                    num++;
                    if (thumbsImageID != null) {
                        thumbsIDs.add(thumbsID);
                        thumbsDatas.add(thumbsData);
                    }
                } while (imageCursor.moveToNext());
                Log.d("ID는" , thumbsID);
            }

            imageCursor.close();
            return;
        }

        private String getImageInfo(String ImageData, String Location, String thumbID) {
            String imageDataPath = null;
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};
            Cursor imageCursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, "_ID='" + thumbID + "'", null, null);

            if (imageCursor != null && imageCursor.moveToFirst()) {
                if (imageCursor.getCount() > 0) {
                    int imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    imageDataPath = imageCursor.getString(imgData);
                }
            }
            imageCursor.close();
            return imageDataPath;
        }
    }
}

