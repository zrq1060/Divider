package com.zrq.divider;

import android.graphics.drawable.Drawable;

/**
 * 描述:
 *
 * @author zhangrq
 * 2019/5/10 13:56
 */
@SuppressWarnings("unused")
public interface DividerBuilderInterface<Builder, Divider> {
    /**
     * 设置线宽
     */
    Builder width(int lineWidth);

    /**
     * 设置线高
     */
    Builder height(int lineHeight);

    /**
     * 同时设置线宽、线高
     */
    Builder widthAndHeight(int lineSize);

    /**
     * 设置线颜色，和drawable二选一
     */
    Builder color(int lineColor);

    /**
     * 设置线背景，和color二选一
     */
    Builder drawable(Drawable dividerDrawable);

    /**
     * 设置头的数量
     */
    Builder headerCount(int headerCount);

    /**
     * 设置尾的数量
     */
    Builder footerCount(int footerCount);

    /**
     * 不绘制线，只保留距离
     */
    Builder notDraw(boolean isNotDraw);

    /**
     * 返回Divider
     */
    Divider build();
}
