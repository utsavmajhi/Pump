package com.huxq17.download.demo.widget.slidingtab;

import android.view.View;
import android.view.ViewGroup;

/**
 * {@link SlidingTabLayout} Adapter
 *
 * Author: huxiaoqian
 * Version V1.0
 * Date: 2019/6/6
 * Description:
 * Modification History:
 * Date Author Version Description
 * -----------------------------------------------------------------------------------
 * 2019/6/6 huxiaoqian 1.0
 * Why & What is modified:
 */
public interface TabAdapter <T extends View> {
    void onAttach(ViewGroup parent);
    T onCreateView();
    void onBind(T view, int position);
    void onPageChanged(T view, int position, float offset);
}
