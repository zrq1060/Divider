package com.zrq.dividerdemo;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 描述:
 *
 * @author zhangrq
 * 2018/3/22 16:18
 */

public class TestAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private int layoutManagerState;

    TestAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, String item) {
        int position = holder.getAdapterPosition();
        holder.itemView.setBackgroundColor(Color.RED);
        // 设置Item宽高
        switch (layoutManagerState) {
            case 1:
                // LinearLayoutManager VERTICAL
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(400, 200));
                break;
            case 2:
                // LinearLayoutManager HORIZONTAL
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(400, 200));
                break;
            case 3:
                // GridLayoutManager VERTICAL
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
                break;
            case 4:
                // GridLayoutManager HORIZONTAL
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT));
                break;
            case 5:
                // StaggeredGridLayoutManager VERTICAL
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, position % 2 == 0 ? 200 : 700));
                break;
            case 6:
                // StaggeredGridLayoutManager HORIZONTAL
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(position % 2 == 0 ? 200 : 700, ViewGroup.LayoutParams.MATCH_PARENT));
                break;
        }
        // 设置数据
        holder.setText(R.id.textView, item);
    }

    public void setLayoutManagerState(int layoutManagerState) {
        this.layoutManagerState = layoutManagerState;
    }
}
