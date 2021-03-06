package com.juggist.baseandroid.ui.user.fragment;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.juggist.baseandroid.R;
import com.juggist.baseandroid.present.user.UserLoginPresent;
import com.juggist.baseandroid.ui.HomeActivity;
import com.juggist.baseandroid.ui.user.UserPasswordForgetActivity;
import com.juggist.baseandroid.utils.ToastUtil;
import com.juggist.baseandroid.view.ButtonCountDown;
import com.juggist.baseandroid.view.ClearEditText;
import com.juggist.jcore.Constants;
import com.juggist.jcore.base.BaseFragment;
import com.juggist.jcore.utils.KeyboardUtils;
import com.juggist.jcore.utils.RegexUtils;

import butterknife.BindView;
import butterknife.OnClick;


public class UserLoginFragment extends BaseFragment {


    @BindView(R.id.et_phone)
    ClearEditText etPhone;
    @BindView(R.id.et_psw)
    ClearEditText etPsw;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_psw_forfet)
    TextView tvPswForfet;
    @BindView(R.id.btn_code_send)
    ButtonCountDown btnCodeSend;



    private UserLoginContract.Present present;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_login;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        new UserLoginPresent(new ViewModel());
        //初始化计时器
        btnCodeSend.setTag(Constants.SP.COUNT_DOWN_TIME_LOGIN,Constants.CountDownTime.LOGIN_AUTH_CODE,Constants.CountDownTime.COUNT_DOWN_INTERVAL);

    }


    @Override
    public void onResume() {
        super.onResume();
        //解决在onResume里无法唤起软键盘
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtils.showSoftInput(getActivity());
            }
        },500);

    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardUtils.hideSoftInput(getActivity());
    }

    @Override
    public void onDestroyView() {
        btnCodeSend.destory();
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            KeyboardUtils.hideSoftInput(getActivity());
        } else {
            KeyboardUtils.showSoftInput(getActivity());
        }
    }

    @OnClick({R.id.btn_login, R.id.tv_psw_forfet,R.id.btn_code_send})
    public void onViewClicked(View view) {
        String phone = etPhone.getText().toString();
        String code = etPsw.getText().toString();
        switch (view.getId()) {
            case R.id.btn_login:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showLong(R.string.toast_login_phone_empty);
                    return;
                }
                if (!RegexUtils.isMobileExact(phone)) {
                    ToastUtil.showLong(R.string.toast_login_phone_un_right);
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showLong(R.string.toast_auth_code_empty);
                    return;
                }
                KeyboardUtils.hideSoftInput(getActivity());
                present.login(phone, code);
                break;
            case R.id.tv_psw_forfet:
                startActivity(new Intent(getActivity(), UserPasswordForgetActivity.class));
                break;
            case R.id.btn_code_send:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showLong(R.string.toast_login_phone_empty);
                    return;
                }
                if (!RegexUtils.isMobileExact(phone)) {
                    ToastUtil.showLong(R.string.toast_login_phone_un_right);
                    return;
                }
                present.getAuthCode(phone);
                btnCodeSend.start();
                break;
        }
    }


        private class ViewModel implements UserLoginContract.View {

        @Override
        public void getAuthCodeSucceed(String authCode) {
            ToastUtil.showLong(R.string.toast_auth_code);
        }

        @Override
        public void getAuthCodeFail(String extMsg) {
            btnCodeSend.cancel();
            showErrorDialog(extMsg);
        }

        @Override
        public void loginSucceed() {
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().finish();
        }

        @Override
        public void loginFail(String extMsg) {
            showErrorDialog(extMsg);
        }

        @Override
        public void setPresent(UserLoginContract.Present present) {
            UserLoginFragment.this.present = present;
        }

        @Override
        public void showErrorDialog(String message) {
            ToastUtil.showLong(message);
        }

        @Override
        public void showLoading() {

        }

        @Override
        public void dismissLoading() {

        }
    }

}