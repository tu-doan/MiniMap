package com.dmtu.user.miniproject;

import java.util.List;

public interface GetDirectionListener {
    void onGetDirectionStart();
    void onGetDirectionSuccess(List<Route> routes);
}
