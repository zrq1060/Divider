package com.zrq.divider;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * 描述: Grid通用分割线
 *
 * @author zhangrq
 * 2018/4/18 16:08
 */
@SuppressWarnings("unused")
public class GridDivider extends RecyclerView.ItemDecoration {

    private Drawable dividerDrawable;
    private final int DEFAULT_LINE_WIDTH = 10;
    private final int DEFAULT_LINE_HEIGHT = 20;

    private int lineWidth = DEFAULT_LINE_WIDTH;// 线的宽度
    private int lineHeight = DEFAULT_LINE_HEIGHT;// 线的高度
    private int headerCount = 0;// 头的数量
    private int footerCount = 0;// 尾的数量
    private boolean isNotDraw;// 设置是否不绘制线

    GridDivider() {
        dividerDrawable = new ColorDrawable(Color.GRAY);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (isSkipDraw(parent, view))
            return;// 跳过，不绘制
        outRect.set(getLineRect(view, parent));
    }

    private Rect getLineRect(View view, RecyclerView parent) {
        int spanCount = getSpanCount(parent);// 水平个数，线性布局为-1
        if (spanCount == -1) return new Rect();// 不是GridLayoutManager
        int currentPosition = parent.getChildAdapterPosition(view);
        int orientation = ((GridLayoutManager) parent.getLayoutManager()).getOrientation();
        int childCount = parent.getAdapter().getItemCount();// 总个数
        int left;
        int top;
        int right;
        int bottom;
        if (orientation == GridLayoutManager.VERTICAL) {
            // 垂直
            int itemOffsetWidth = lineWidth * (spanCount - 1) / spanCount;// item 偏移的宽
            int itemOffsetHeight = lineHeight;// item 偏移的高
            if (isNotDrawLeft(view, parent, currentPosition, spanCount, childCount)) {
                // 如果是第一列，则不需要绘制左边
                left = 0;
                right = itemOffsetWidth;
            } else {
                left = (lineWidth - itemOffsetWidth) * (currentPosition % spanCount);
                right = itemOffsetWidth - left;
            }
            top = 0;
            bottom = itemOffsetHeight;
            if (isNotDrawBottom(view, parent, currentPosition, spanCount, childCount)) {
                // 如果是最后一行，则不需要绘制底部
                bottom = 0;
            }
        } else {
            // 水平
            int itemOffsetWidth = lineWidth;// item 偏移的宽
            int itemOffsetHeight = (int) Math.ceil(lineHeight * (spanCount - 1) * 1.0 / spanCount);// item 偏移的高
            left = 0;
            right = itemOffsetWidth;
            if (isNotDrawRight(view, parent, currentPosition, spanCount, childCount)) {
                // 如果是最后一列，则不需要绘制右边
                right = 0;
            }
            if (isNotDrawTop(view, parent, currentPosition, spanCount, childCount)) {
                // 如果是第一行，则不需要绘制顶部
                top = 0;
                bottom = itemOffsetHeight;
            } else {
                top = (lineHeight - itemOffsetHeight) * (currentPosition % spanCount);
                bottom = itemOffsetHeight - top;
//                if (currentPosition == childCount - 1)
//                    // 最后一个，显示整条线的高度
//                    bottom = lineHeight;
            }
        }
        Log.e("ddddddd", currentPosition + "==" + left + "==" + top + "==" + right + "==" + bottom);
        return new Rect(left, top, right, bottom);
    }


    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (isNotDraw) return;// 不绘制线，直接返回
        drawHorizontal(canvas, parent, lineWidth, lineHeight);
//        drawVertical(canvas, parent, lineWidth, lineHeight);
    }

    /**
     * 是否不绘制左部
     *
     * @param view            当前的view，StaggeredGridLayoutManager 用
     * @param parent          RecyclerView
     * @param currentPosition 当前的位置，GridLayoutManager、LinearLayoutManager用
     * @param spanCount       列数
     * @param adapterCount    adapter的总数
     * @return 返回true代表不绘制左部，返回false，代表绘制左部
     */
    private boolean isNotDrawLeft(View view, RecyclerView parent, int currentPosition, int spanCount, int adapterCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // GridLayoutManager
            currentPosition -= getHeaderCount();// 去掉头的数量
            adapterCount -= getHeaderCount() + getFooterCount();// 去掉头、尾的数量
            // 判断最后一个是否绘制
            if (((GridLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
                // 垂直，判断是否是第一列
                return (currentPosition + 1) % spanCount == 1;
            } else {
                // 水平，判断是否是第一列
                return currentPosition < spanCount;
            }
        }
        return false;
    }

    /**
     * 是否不绘制顶部
     *
     * @param view            当前的view，StaggeredGridLayoutManager 用
     * @param parent          RecyclerView
     * @param currentPosition 当前的位置，GridLayoutManager、LinearLayoutManager用
     * @param spanCount       列数
     * @param adapterCount    adapter的总数
     * @return 返回true代表不绘制顶部，返回false，代表绘制顶部
     */
    private boolean isNotDrawTop(View view, RecyclerView parent, int currentPosition, int spanCount, int adapterCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // GridLayoutManager
            currentPosition -= getHeaderCount();// 去掉头的数量
            adapterCount -= getHeaderCount() + getFooterCount();// 去掉头、尾的数量
            // 判断第一个是否绘制
            if (((GridLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
                // 垂直，判断是不是第一个
                return currentPosition < spanCount;
            } else {
                // 水平，判断是不是第一行
                return (currentPosition + 1) % spanCount == 1;
            }
        }
        return false;
    }

    /**
     * 是否不绘制右部
     *
     * @param view            当前的view，StaggeredGridLayoutManager 用
     * @param parent          RecyclerView
     * @param currentPosition 当前的位置，GridLayoutManager、LinearLayoutManager用
     * @param spanCount       列数
     * @param adapterCount    adapter的总数
     * @return 返回true代表不绘制右部，返回false，代表绘制右部
     */
    private boolean isNotDrawRight(View view, RecyclerView parent, int currentPosition, int spanCount, int adapterCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // GridLayoutManager
            currentPosition -= getHeaderCount();// 去掉头的数量
            adapterCount -= getHeaderCount() + getFooterCount();// 去掉头、尾的数量
            // 判断最后一个是否绘制
            if (((GridLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
                // 垂直，判断是否是最后一列
                return (currentPosition + 1) % spanCount == 0;
            } else {
                // 水平，判断是不是最后的
                if (adapterCount % spanCount == 0)
                    return currentPosition >= adapterCount - spanCount;
                else
                    return currentPosition >= adapterCount - adapterCount % spanCount;
            }
        }
        return false;
    }

    /**
     * 是否不绘制底部
     *
     * @param view            当前的view，StaggeredGridLayoutManager 用
     * @param parent          RecyclerView
     * @param currentPosition 当前的位置，GridLayoutManager、LinearLayoutManager用
     * @param spanCount       列数
     * @param adapterCount    adapter的总数
     * @return 返回true代表不绘制底部，返回false，代表绘制底部
     */
    private boolean isNotDrawBottom(View view, RecyclerView parent, int currentPosition, int spanCount, int adapterCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // GridLayoutManager
            currentPosition -= getHeaderCount();// 去掉头的数量
            adapterCount -= getHeaderCount() + getFooterCount();// 去掉头、尾的数量
            // 判断最后一个是否绘制
            if (((GridLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
                // 垂直，判断是不是最后的
                if (adapterCount % spanCount == 0)
                    return currentPosition >= adapterCount - spanCount;
                else
                    return currentPosition >= adapterCount - adapterCount % spanCount;
            } else {
                // 水平，判断是不是最后一列
                return (currentPosition + 1) % spanCount == 0;
            }
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
        if (!(parent.getLayoutManager() instanceof GridLayoutManager)) return;
        int orientation = ((GridLayoutManager) parent.getLayoutManager()).getOrientation();
        canvas.save();
        int spanCount = getSpanCount(parent);// 水平个数，线性布局为-1
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
            Rect lineRect = getLineRect(child, parent);
            // 绘制左右线
            int lineTop = child.getTop() - lineRect.top;
            int lineBottom = child.getBottom() + lineRect.bottom;
            // --绘制左线
            int lineLeft = child.getLeft();
            int lineRight = lineLeft - lineRect.left;
            dividerDrawable.setBounds(lineLeft, lineTop, lineRight, lineBottom);
            dividerDrawable.draw(canvas);
            // --绘制右线
            lineRight = child.getRight();
            lineLeft = lineRight + lineRect.right;
            Drawable rightDividerDrawable = /*orientation == GridLayoutManager.VERTICAL && currentPosition == adapterCount - 1 ? new ColorDrawable(Color.TRANSPARENT) :*/ dividerDrawable;
            rightDividerDrawable.setBounds(lineLeft, lineTop, lineRight, lineBottom);
            rightDividerDrawable.draw(canvas);
            // 绘制上下线
            lineLeft = child.getLeft() - lineRect.left;
            lineRight = child.getRight() + lineRect.right;
            // --绘制上线
            lineTop = child.getTop();
            lineBottom = lineTop - lineRect.top;
            dividerDrawable.setBounds(lineLeft, lineTop, lineRight, lineBottom);
            dividerDrawable.draw(canvas);
            // --绘制下线
            lineBottom = child.getBottom();
            lineTop = lineBottom + lineRect.bottom;
            Drawable bottomDividerDrawable = /*orientation == GridLayoutManager.HORIZONTAL && currentPosition == adapterCount - 1 ? new ColorDrawable(Color.TRANSPARENT) :*/ dividerDrawable;
            bottomDividerDrawable.setBounds(lineLeft, lineTop, lineRight, lineBottom);
            bottomDividerDrawable.draw(canvas);
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
        int spanCount = getSpanCount(parent);// 水平个数，线性布局为-1
        int childCount = parent.getChildCount();
        int adapterCount = parent.getAdapter().getItemCount();// 总个数
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int currentPosition = parent.getChildAdapterPosition(child);
            if (isSkipDraw(parent, child))
                // 跳过、不绘制右部，直接返回
                continue;
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (!isNotDrawRight(child, parent, currentPosition, spanCount, adapterCount)) {
                // 不绘制右边
                if (isNotDrawBottom(child, parent, currentPosition, spanCount, adapterCount))
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
     * 获取列数
     *
     * @param parent RecyclerView
     * @return 列数
     */
    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
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
    public static GridDivider.Builder builder() {
        return new GridDivider.Builder();
    }

    public static class Builder {

        private final GridDivider divider;

        Builder() {
            divider = new GridDivider();
        }

        /**
         * 设置线宽
         */
        public Builder width(int lineWidth) {
            divider.setLineWidth(lineWidth);
            return this;
        }

        /**
         * 设置线高
         */
        public Builder height(int lineHeight) {
            divider.setLineHeight(lineHeight);
            return this;
        }

        /**
         * 同时设置线宽、线高
         */
        public Builder widthAndHeight(int lineSize) {
            divider.setLineWidth(lineSize);
            divider.setLineHeight(lineSize);
            return this;
        }

        /**
         * 设置线颜色，和drawable二选一
         */
        public Builder color(int lineColor) {
            divider.setLineColor(lineColor);
            return this;
        }

        /**
         * 设置线背景，和color二选一
         */
        public Builder drawable(Drawable dividerDrawable) {
            divider.setDividerDrawable(dividerDrawable);
            return this;
        }

        /**
         * 设置头的数量
         */
        public Builder headerCount(int headerCount) {
            divider.setHeaderCount(headerCount);
            return this;
        }

        /**
         * 设置尾的数量
         */
        public Builder footerCount(int footerCount) {
            divider.setFooterCount(footerCount);
            return this;
        }

        /**
         * 返回Divider
         */
        public GridDivider build() {
            return this.divider;
        }

    }
}
