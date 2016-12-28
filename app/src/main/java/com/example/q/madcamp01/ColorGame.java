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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by q on 2016-12-27.
 */

public class ColorGame extends Fragment {
    public Context mContext;
    GridView gv;
    TextView trial;
    ColorGame.ImageAdapter mAdapter;
    int clickedPosition = -1;
    int numTrial = 0;
    private static final int NUM_COLOR = 3;
    private static final int END_OF_GAME = -2;

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
        trial = (TextView) rootView.findViewById(R.id.trial);

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
                colors[i] = (int)(Math.random() * NUM_COLOR);
            }
        }

        public final void callImageViewer(int selectedIndex){
            if (selectedIndex == clickedPosition) {
                Toast.makeText(getActivity(), "같은 셀을 연속해서 클릭할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
            else if (clickedPosition != END_OF_GAME) {
                // colors[selectedIndex]를 0으로 바꾼다(한바퀴 돌아서 처음 상태로).
                if (colors[selectedIndex] == NUM_COLOR-1) {
                    colors[selectedIndex] = 0;
                }
                // colors[selectedIndex]를 1 더한다.
                else {
                    colors[selectedIndex]++;
                }
                clickedPosition = selectedIndex;
                increaseTrial();
                gv.setAdapter(mAdapter);
            }
            if (isFinished()) {
                // 텍스트뷰로 트라이얼 개수 출력, 리게임 버튼 표시, 더이상 눌러도 변하지 않게끔.
                Toast.makeText(getActivity(), "끝났음 ㅅㄱ.", Toast.LENGTH_SHORT).show();
                clickedPosition = END_OF_GAME;
            }
        }

        public boolean isFinished() {
            int temp = -1;
            for (int i = 0; i < 16; i++) {
                if (temp == -1) {
                    temp = colors[i];
                } else if (temp != colors[i]) {
                    return false;
                }
            }
            return true;
        }

        public void increaseTrial() {
            numTrial++;
            trial.setText("Trial : "+String.valueOf(numTrial));
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
