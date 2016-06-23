package com.gospelware.testcolormatrix;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    private static final int GET_IMAGE_PATH_REQUEST = 123;

    @OnClick(R.id.btnAdd)
    void onAdd() {
        Intent it = new Intent();
        it.setType("image/*");
        it.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(it, GET_IMAGE_PATH_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_IMAGE_PATH_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Intent it = new Intent(this, CanvasActivity.class);
            it.setData(uri);
            startActivity(it);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
