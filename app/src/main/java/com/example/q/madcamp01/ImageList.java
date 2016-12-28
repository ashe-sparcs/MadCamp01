package com.example.q.madcamp01;


        import java.lang.reflect.Array;
        import java.util.ArrayList;

        import android.Manifest;
        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.content.pm.PackageManager;
        import android.media.Image;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.provider.ContactsContract;
        import android.support.v4.app.Fragment;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.util.DisplayMetrics;
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
    private SwipeRefreshLayout swipeContainer;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            new GetImageTask().execute(false);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                new GetImageTask().execute(false);
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
        private ArrayList<String> thumbsDataList;
        private ArrayList<String> thumbsIDList;

        ImageAdapter(Context c, ArrayList<String> data_list, ArrayList<String> id_list, ArrayList<Bitmap> nail_list) {
            mContext = c;
            thumbsDataList = data_list;
            thumbsIDList = id_list;
        }

        public void addItem(String id, String data) {
            thumbsDataList.add(data);
            thumbsIDList.add(id);
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
            if (!thumbsIDs.isEmpty()) {
                thumbsIDs.clear();
            }
            if (!thumbsDatas.isEmpty()) {
                thumbsDatas.clear();
            }
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};

            Cursor imageCursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);
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

    public class GetImageTask extends AsyncTask<Boolean, String, Boolean> {

        GridView gv;
        SwipeRefreshLayout swipeContainer;
        ArrayList<String> data_list;
        ArrayList<String> id_list;
        ArrayList<Bitmap> nail_list;
        ImageAdapter m_adapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data_list = new ArrayList<>();
            id_list = new ArrayList<>();
            nail_list = new ArrayList<>();
            swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer);
            gv = (GridView) getActivity().findViewById(R.id.ImgGridView);
            gv.setPadding(0,0,0,getSoftButtonsBarHeight());
            m_adapter = new ImageAdapter(getActivity(), data_list, id_list, nail_list);
            gv.setAdapter(m_adapter);
        }

        @Override
        protected Boolean doInBackground(Boolean... isRefresh) {
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE};

            Cursor imageCursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);
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
                        publishProgress(thumbsID, thumbsData);
                    }
                } while (imageCursor.moveToNext());
            }
            imageCursor.close();
            return isRefresh[0];
        }

        @Override
        protected void onProgressUpdate(String... thumbs) {
            // 파일 다운로드 퍼센티지 표시 작업
            m_adapter.addItem(thumbs[0], thumbs[1]);
            m_adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Boolean isRefresh) {
            // doInBackground 에서 받아온 total 값 사용 장소
            gv.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    m_adapter.callImageViewer(position);
                }
            });
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    new GetImageTask().execute(true);
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            if (isRefresh) {
                swipeContainer.setRefreshing(false);
            }
        }
    }
}

