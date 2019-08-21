package com.canghai.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.canghai.R;
import com.canghai.ui.MainActivity;

/**
 * Created by dell on 2019/1/9.
 */

public class AlertDialogUtils {
    // 带“是”和“否”的提示框

    /***
     * 双选择提示框
     * @param context 上下文
     * @param title  提示框名
     * @param message  提示内容
     * @param sureClick 确定事件
     * @param abortClick  取消事件
     */
    public static void isSure(Context context,String title,String message,
                               DialogInterface.OnClickListener sureClick,
                               DialogInterface.OnClickListener abortClick) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)//注意这里的参数要是dialog依赖的acitivity的引用;
            //.setView(View.inflate(this, R.layout.activity_main,null))
            .setTitle("视频通话请求")
            .setMessage("PC端发起现场直播请求，是否开启直播？")
            .setPositiveButton("确定", null)
            .setNegativeButton("取消", null)
            .create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }
    // 简单消息提示框
    private void showExitDialog01(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("标题")
                .setMessage("简单的消息提示框")
                .setPositiveButton("确定", null)
                .show();
    }
    // 带“是”和“否”的提示框
    private void showExitDialog02(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("带确定键的提示框")
                .setMessage("确定吗")
                .setPositiveButton("是", null)
                .setNegativeButton("否", null)
                .show();
    }
    // 可输入文本的提示框
    private void showExitDialog03(Context context){
        final EditText edt = new EditText(context);
        edt.setMinLines(3);
        new AlertDialog.Builder(context)
                .setTitle("请输入")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(edt)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        String value = edt.getText().toString();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 单选提示框
    private void showExitDialog04(Context context){
        new AlertDialog.Builder(context)
                .setTitle("请选择")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(new String[]{"选项1","选项2","选项3","选项4","选项5","选项6"}, -1, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0, int arg1) {
                        String value = "";
                        switch (arg1) {
                            case 0:value = "选择了一";break;
                            case 1:value = "选择了二";break;
                            case 2:value = "选择了三";break;
                            default: break;
                        }
                        arg0.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    // 多选提示框
    private void showExitDialog05(Context context){
        new AlertDialog.Builder(context)
                .setTitle("多选框")
                .setMultiChoiceItems(new String[]{"选项1","选项2","选项3","选项4","选项5","选项6"}, null, null)
                .setPositiveButton("确定",null)
                .setNegativeButton("取消",null)
                .show();
    }
    // 列表对话框
    private void showExitDialog06(Context context){
        new AlertDialog.Builder(context)
                .setTitle("列表框")
                .setItems(new String[]{"列表1","列表2","列表3","列表4","列表5"}, null)
                .setNegativeButton("确定", null)
                .show();
    }
    // 显示图片的对话框
    private void showExitDialog07(Context context){
        ImageView img = new ImageView(context);
        img.setImageResource(R.drawable.call_video);
        new AlertDialog.Builder(context)
                .setTitle("图片框")
                .setView(img)
                .setPositiveButton("确定", null)
                .show();
    }
}
