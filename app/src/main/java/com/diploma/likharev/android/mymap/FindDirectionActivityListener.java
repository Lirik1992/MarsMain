package com.diploma.likharev.android.mymap;

import java.util.List;
/**
 * Created by Lirik on 27.04.2017.
 */

public interface FindDirectionActivityListener {
    void onFindDirectionActivityStart();

    void onFindDirectionActivitySuccess(List<Route> route);

}
