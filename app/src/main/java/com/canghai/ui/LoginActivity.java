package com.canghai.ui;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.canghai.utils.BaseApplication;
import com.canghai.R;
import butterknife.BindView;
import butterknife.ButterKnife;
public class LoginActivity extends BaseApplication implements View.OnClickListener{
    @BindView(R.id.btn_login)
    Button btn_login;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        btn_login.setOnClickListener(this);

        Intent intent = new Intent(this,CreateQrcActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                login();
                break;
        }
    }
    private void login(){
        // 给bnt1添加点击响应事件
        Intent intent =new Intent(LoginActivity.this,MainActivity.class);
        //启动
        startActivity(intent);
        finish();
    }
}
