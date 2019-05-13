package com.zrq.divider;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 描述: 线性通用分割线
 *
 * @author zhangrq
 * 2018/4/18 16:08
 */
@SuppressWarnings("unused")
public class LinearDivider extends RecyclerView.ItemDecoration {

    private Drawable dividerDrawable;
    private final int DEFAULT_LINE_WIDTH = 10;
    private final int DEFAULT_LINE_HEIGHT = 20;

    private int lineWidth = DEFAULT_LINE_WIDTH;// 线的宽度
    private int lineHeight = DEFAULT_LINE_HEIGHT;// 线的高度
    private int headerCount = 0;// 头的数量
    private int footerCount = 0;// 尾的数量
    private boolean isNotDraw;// 设置是否不绘制线

    LinearDivider() {
        dividerDrawable = new ColorDrawable(Color.GRAY);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (isSkipDraw(parent, view))
            return;// 跳过，不绘制
        int currentPosition = parent.getChildAdapterPosition(view);
        int childCount = parent.getAdapter().getItemCount();// 总个数
        int right = lineWidth;
        int bottom = lineHeight;
        if (isNotDrawRight(view, parent, currentPosition, childCount))
            // 如果是最后一列，则不需要绘制右边
            right = 0;
        if (isNotDrawBottom(view, parent, currentPosition, childCount))
            // 如果是最后一行，则不需要绘制底部
            bottom = 0;
        outRect.set(0, 0, right, bottom);
    }


    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (isNotDraw) return;// 不绘制线，直接返回
        drawHorizontal(canvas, parent, lineWidth, lineHeight);
        drawVertical(canvas, parent, lineWidth, lineHeight);
    }

    /**
     * 是否不绘制右部
     *
     * @param view            当前的view，StaggeredGridLayoutManager 用
     * @param parent          RecyclerView
     * @param currentPosition 当前的位置，GridLayoutManager、LinearLayoutManager用
     * @param adapterCount    adapter的总数
     * @return 返回true代表不绘制右部，返回false，代表绘制右部
     */
    private boolean isNotDrawRight(View view, RecyclerView parent, int currentPosition, int adapterCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            // LinearLayoutManager
            // 判断最后一个是否绘制，垂直，不绘制右边，直接返回true,水平，判断，是否是最后一个
            return ((LinearLayoutManager) layoutManager).getOrientation() == LinearLayout.VERTICAL || currentPosition == adapterCount - getFooterCount() - 1;
        }
        return false;
    }

    /**
     * 是否不绘制底部
     *
     * @param view            当前的view，StaggeredGridLayoutManager 用
     * @param parent          RecyclerView
     * @param currentPosition 当前的位置，GridLayoutManager、LinearLayoutManager用
     * @param adapterCount    adapter的总数
     * @return 返回true代表不绘制底部，返回false，代表绘制底部
     */
    private boolean isNotDrawBottom(View view, RecyclerView parent, int currentPosition, int adapterCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            // LinearLayoutManager
            // 判断最后一个是否绘制，垂直，判断是否是最后一行,水平，直接返回true，不绘制底部
            return ((LinearLayoutManager) layoutManager).getOrientation() != LinearLayout.VERTICAL || currentPosition == adapterCount - getFooterCount() - 1;
        }
        return false;
    }

    /**
     * 绘制水平线
     *
     * @param canvas     画布
     * @param parent     RecyclerView
     * @param lineWidth  线宽
     * @param lineHeight 线高
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent, int lineWidth, int lineHeight) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        canvas.save();
        int childCount = parent.getChildCount();// 显示的个数
        int adapterCount = parent.getAdapter().getItemCount();// 总个数
        if (parent.getClipToPadding()) {
            canvas.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                    parent.getWidth() - parent.getPaddingRight(),
                    parent.getHeight() - parent.getPaddingBottom());
        }

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int currentPosition = parent.getChildAdapterPosition(child);
            if (isSkipDraw(parent, child))
                // 跳过，直接返回
                continue;
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (!isNotDrawBottom(child, parent, currentPosition, adapterCount)) {
                // 绘制底部
                int bottomLineWidth = isNotDrawRight(child, parent, currentPosition, adapterCount) ? 0 : lineWidth;// 不绘制右部，公共区域不绘制
                // 绘制下线
                final int downLeft = child.getLeft() - params.leftMargin;
                final int downTop = child.getBottom() + params.bottomMargin;
                final int downRight = child.getRight() + params.rightMargin + bottomLineWidth;// 公共区域绘制
                final int downBottom = downTop + lineHeight;
                dividerDrawable.setBounds(downLeft, downTop, downRight, downBottom);
                dividerDrawable.draw(canvas);
            }
        }
        canvas.restore();
    }

    /**
     * 绘制垂直线
     *
     * @param canvas     画布
     * @param parent     RecyclerView
     * @param lineWidth  线宽
     * @param lineHeight 线高
     */
    private void drawVertical(Canvas canvas, RecyclerView parent, int lineWidth, int lineHeight) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        canvas.save();
        if (parent.getClipToPadding()) {
            canvas.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                    parent.getWidth() - parent.getPaddingRight(),
                    parent.getHeight() - parent.getPaddingBottom());
        }
        int childCount = parent.getChildCount();
        int adapterCount = parent.getAdapter().getItemCount();// 总个数
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int currentPosition = parent.getChildAdapterPosition(child);
            if (isSkipDraw(parent, child))
                // 跳过、不绘制右部，直接返回
                continue;
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (!isNotDrawRight(child, parent, currentPosition, adapterCount)) {
                // 绘制右边
                if (isNotDrawBottom(child, parent, currentPosition, adapterCount))
                    // 不绘制底部，公共区域不绘制
                    lineHeight = 0;
                final int left = child.getRight() + params.rightMargin;
                final int top = child.getTop() - params.topMargin;
                final int right = left + lineWidth;
                final int bottom = child.getBottom() + params.bottomMargin + lineHeight;// 公共区域水平绘制
                dividerDrawable.setBounds(left, top, right, bottom);
                dividerDrawable.draw(canvas);
            }
        }
        canvas.restore();
    }

    /**
     * 是否跳过绘画
     *
     * @param parent RecyclerView
     * @param view   当前View
     */
    private boolean isSkipDraw(RecyclerView parent, View view) {
        int currentPosition = parent.getChildAdapterPosition(view);// 当前item总位置
        int adapterCount = parent.getAdapter().getItemCount();
        return currentPosition < getHeaderCount() || currentPosition >= adapterCount - getFooterCount();
    }

    /**
     * 获取线宽
     */
    public int getLineWidth() {
        return lineWidth;
    }

    /**
     * 设置线宽
     */
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * 获取线高
     */
    public int getLineHeight() {
        return lineHeight;
    }

    /**
     * 设置线高
     */
    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    /**
     * 获取线Drawable
     */
    public Drawable getDividerDrawable() {
        return dividerDrawable;
    }

    /**
     * 设置线Drawable，和setLineColor()二选一
     */
    public void setDividerDrawable(Drawable dividerDrawable) {
        this.dividerDrawable = dividerDrawable;
    }

    /**
     * 设置线颜色，和setDividerDrawable()二选一
     */
    public void setLineColor(int lineColor) {
        this.dividerDrawable = new ColorDrawable(lineColor);
    }

    /**
     * 获取头数量
     */
    private int getHeaderCount() {
        return headerCount;
    }

    /**
     * 设置头数量，即头部跳过绘制
     */
    public void setHeaderCount(int headerCount) {
        this.headerCount = headerCount;
    }

    /**
     * 获取尾数量
     */
    private int getFooterCount() {
        return footerCount;
    }

    /**
     * 设置尾数量，即尾部跳过绘制
     */
    public void setFooterCount(int footerCount) {
        this.footerCount = footerCount;
    }

    public boolean isNotDraw() {
        return isNotDraw;
    }

    public void setNotDraw(boolean notDraw) {
        isNotDraw = notDraw;
    }

    /**
     * Divider的构建者
     */
    public static LinearDivider.Builder builder() {
        return new LinearDivider.Builder();
    }

    public static class Builder implements DividerBuilderInterface<Builder, LinearDivider> {

        private final LinearDivider divider;

        Builder() {
            divider = new LinearDivider();
        }

        /**
         * 设置线宽
         */
        @Override
        public Builder width(int lineWidth) {
            divider.setLineWidth(lineWidth);
            return this;
        }

        /**
         * 设置线高
         */
        @Override
        public Builder height(int lineHeight) {
            divider.setLineHeight(lineHeight);
            return this;
        }

        /**
         * 同时设置线宽、线高
         */
        @Override
        public Builder widthAndHeight(int lineSize) {
            divider.setLineWidth(lineSize);
            divider.setLineHeight(lineSize);
            return this;
        }

        /**
         * 设置线颜色，和drawable二选一
         */
        @Override
        public Builder color(int lineColor) {
            divider.setLineColor(lineColor);
            return this;
        }

        /**
         * 设置线背景，和color二选一
         */
        @Override
        public Builder drawable(Drawable dividerDrawable) {
            divider.setDividerDrawable(dividerDrawable);
            return this;
        }

        /**
         * 设置头的数量
         */
        @Override
        public Builder headerCount(int headerCount) {
            divider.setHeaderCount(headerCount);
            return this;
        }

        /**
         * 设置尾的数量
         */
        @Override
        public Builder footerCount(int footerCount) {
            divider.setFooterCount(footerCount);
            return this;
        }

        /**
         * 设置是否不绘制线，只保留距离
         */
        @Override
        public Builder notDraw(boolean isNotDraw) {
            divider.setNotDraw(isNotDraw);
            return this;
        }

        /**
         * 返回Divider
         */
        @Override
        public LinearDivider build() {
            return this.divider;
        }
    }
}
