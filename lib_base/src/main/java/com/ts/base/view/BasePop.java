package com.ts.base.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.ts.base.R;

import java.util.ArrayList;
import java.util.List;

public class BasePop {
    public static class Item {
        private int icon;
        private String value;

        Item(int icon, String value) {
            this.icon = icon;
            this.value = value;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String value);
    }

    public class ItemAdapter extends BaseAdapter {

        private Context context;
        private List<Item> data;
        private int checkPos;

        ItemAdapter(Context context, List<Item> data, int checkPos) {
            this.context = context;
            this.data = data;
            this.checkPos = checkPos;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return data == null ? null : data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.base_ada_pop_list, parent, false);
                holder = new ViewHolder();
                holder.iconIv = convertView.findViewById(R.id.iconIv);
                holder.checkIv = convertView.findViewById(R.id.checkIv);
                holder.valueTv = convertView.findViewById(R.id.valueTv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Item item = data.get(position);
            if (item.icon != Integer.MIN_VALUE) {
                holder.iconIv.setVisibility(View.VISIBLE);
                holder.iconIv.setImageResource(item.icon);
            } else {
                holder.iconIv.setVisibility(View.GONE);
            }
            holder.valueTv.setText(item.value);
            holder.checkIv.setVisibility(checkPos == position ? View.VISIBLE : View.INVISIBLE);

            return convertView;
        }

        class ViewHolder {
            ImageView iconIv;
            ImageView checkIv;
            TextView valueTv;
        }
    }

    private Context context;
    private List<Item> data;
    private int checkPos;
    private OnItemClickListener onItemClickListener;
    private View anchorView;

    private ListPopupWindow listPopupWindow;

    private BasePop(Context context, List<Item> data, int checkPos, OnItemClickListener onItemClickListener, View anchorView) {
        this.context = context;
        this.data = data;
        this.checkPos = checkPos;
        this.onItemClickListener = onItemClickListener;
        this.anchorView = anchorView;
        init();
    }

    private void init() {
        listPopupWindow = new ListPopupWindow(context);

        ItemAdapter itemAdapter = new ItemAdapter(context, data, checkPos);
        listPopupWindow.setAdapter(itemAdapter);
        listPopupWindow.setAnchorView(anchorView);
        listPopupWindow.setModal(true);
//        listPopupWindow.setContentWidth(DensityUtil.dpToPx(context, R.dimen.dp_130));
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, data.get(position).value);
                }
            }
        });
    }

    public void show() {
        listPopupWindow.show();
    }

    public static class Builder {
        private Context context;
        private List<Item> data;
        private int checkPos;
        private float scale;
        private OnItemClickListener onItemClickListener;
        private View anchorView;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setData(String[] data, int checkPos, OnItemClickListener l) {
            List<Item> items = new ArrayList<>();
            if (data != null) {
                for (String value : data) {
                    items.add(new Item(Integer.MIN_VALUE, value));
                }
            }
            return setData(items, checkPos, l);
        }

        public Builder setData(List<Item> data, int checkPos, OnItemClickListener l) {
            this.data = data;
            this.checkPos = checkPos;
            this.onItemClickListener = l;
            return this;
        }

        public Builder setAnchorView(View anchorView) {
            this.anchorView = anchorView;
            return this;
        }

        public Builder setWidthScale(float scale) {
            this.scale = scale;
            return this;
        }

        public BasePop create() {
            return new BasePop(context, data, checkPos, onItemClickListener, anchorView);
        }
    }

}