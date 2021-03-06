package com.moko.trackerpro.dialog;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.moko.trackerpro.R;
import com.moko.trackerpro.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ResetDialog extends BaseDialog<String> {
    @BindView(R.id.et_password)
    EditText etPassword;
    private final String FILTER_ASCII = "\\A\\p{ASCII}*\\z";

    public ResetDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_reset;
    }

    @Override
    protected void renderConvertView(View convertView, String password) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (!(source + "").matches(FILTER_ASCII)) {
                    return "";
                }

                return null;
            }
        };
        etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8), filter});
    }

    @OnClick({R.id.tv_password_cancel, R.id.tv_password_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_password_cancel:
                dismiss();
                break;
            case R.id.tv_password_ensure:
                if (TextUtils.isEmpty(etPassword.getText().toString())) {
                    ToastUtils.showToast(getContext(), getContext().getString(R.string.password_null));
                    return;
                }
                if (etPassword.getText().toString().length() != 8) {
                    ToastUtils.showToast(getContext(), getContext().getString(R.string.main_password_length));
                    return;
                }
                dismiss();
                if (passwordClickListener != null)
                    passwordClickListener.onEnsureClicked(etPassword.getText().toString());
                break;
        }
    }

    private PasswordClickListener passwordClickListener;

    public void setOnPasswordClicked(PasswordClickListener passwordClickListener) {
        this.passwordClickListener = passwordClickListener;
    }

    public interface PasswordClickListener {

        void onEnsureClicked(String password);
    }

    public void showKeyboard() {
        if (etPassword != null) {
            //?????????????????????
            etPassword.setFocusable(true);
            etPassword.setFocusableInTouchMode(true);
            //??????????????????
            etPassword.requestFocus();
            //?????????????????????
            InputMethodManager inputManager = (InputMethodManager) etPassword
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etPassword, 0);
        }
    }
}
