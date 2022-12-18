package net.nathan.goodtalk.push;

import android.text.TextUtils;

public class Presenter implements IPresenter {
    private IView mView;

    public Presenter(IView view) {
        mView = view;
    }

    @Override
    public void search() {
        String inputString = mView.getInputString();
        if (TextUtils.isEmpty(inputString)) {
            return;
        }

        int hashCode = inputString.hashCode();
        IUserService userService = new UserService();
        String serviceResult = userService.search(hashCode);
        String result = "Result:" + inputString + "-" + serviceResult;
        mView.setResultString(result);
    }
}
