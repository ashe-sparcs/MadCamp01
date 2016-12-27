package com.example.q.madcamp01;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by q on 2016-12-27.
 */

public class ColorGame extends Fragment {
    public Context mContext;
    GridView gv;
    ColorGame.ImageAdapter mAdapter;

    public static ColorGame newInstance() {
        ColorGame fragment = new ColorGame();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_3, container, false);
        gv = (GridView) rootView.findViewById(R.id.board);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ColorGame.ImageAdapter(getActivity());
        gv.setAdapter(mAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mAdapter.callImageViewer(position);
            }
        });
    }

    /**
     * ==========================================
     * Adapter class
     * ==========================================
     */
    public class ImageAdapter extends BaseAdapter {

        int colors[] = new int[16];

        ImageAdapter(Context c) {
            mContext = c;
            for (int i = 0; i < 16; i++) {
                colors[i] = (int)(Math.random() * 3);
            }
        }

        public final void callImageViewer(int selectedIndex){
            if (colors[selectedIndex] == 0) {
                //colors[selectedIndex]를 1로 바꾸고 이미지뷰를 다음 이미지로 바꿈]
                colors[selectedIndex]++;
                gv.setAdapter(mAdapter);
            } else if (colors[selectedIndex] == 1) {
                //colors[selectedIndex]를 2로 바꾸고 이미지뷰를 다음 이미지로 바꿈
                colors[selectedIndex]++;
                gv.setAdapter(mAdapter);
            } else if (colors[selectedIndex] == 2) {
                //colors[selectedIndex]를 0으로 바꾸고 이미지뷰를 맨처음 이미지로 바꿈
                colors[selectedIndex] = 0;
                gv.setAdapter(mAdapter);
            }
        }

        public int getCount() {
            return colors.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            //이미지뷰를 스테이트에 맞는 그림으로 만든다.
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
            if (colors[position] == 0) {
                //위
                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.up));
            } else if (colors[position] == 1) {
                //오른쪽
                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.right));
            } else if (colors[position] == 2) {
                //아래
                imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.down));
            }
            return imageView;
        }
    }
}
