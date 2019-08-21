package com.canghai.ui;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.canghai.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.canghai.utils.BaseApplication;
import com.canghai.utils.StringUtils;
import com.canghai.utils.ZxingUtils;

public class CreateQrcActivity extends BaseApplication implements View.OnClickListener{
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.btn_create)
    Button btn_create;
    @BindView(R.id.pic_test)
    ImageView img_test;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qrc);
        ButterKnife.bind(this);
        btn_create.setOnClickListener(this);

    }
    private void create(){
        String content = et_content.getText()!=null?et_content.getText().toString():"";
        Bitmap bitmap = ZxingUtils.createBitmap(content);
        img_test.setImageBitmap(bitmap);

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_create:
                create();
                break;
        }
    }
}
