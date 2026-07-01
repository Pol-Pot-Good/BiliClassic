package tv.biliclassic.tv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tv.biliclassic.R;

public class TvGridAdapter extends BaseAdapter {

    private Context context;
    private String[] titles;
    private int[] icons;
    private OnTileClickListener listener;
    private FrameLayout rootContainer;

    public interface OnTileClickListener {
        void onTileClick(String label);
    }

    public TvGridAdapter(Context context) {
        this.context = context;
        this.titles = new String[]{"登录", "推荐", "时间线", "收藏", "搜索", "历史", "设置"};
        this.icons = new int[]{
                R.drawable.ic_tv_user,
                R.drawable.ic_tv,
                R.drawable.ic_tv_timeline,
                R.drawable.ic_tv_star,
                R.drawable.ic_tv_search,
                R.drawable.ic_tv_history,
                R.drawable.ic_action_refresh
        };
    }

    public void setRootContainer(FrameLayout container) {
        this.rootContainer = container;
    }

    public void setOnTileClickListener(OnTileClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public String getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View itemView;
        ViewHolder holder;
        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_tv_tile, parent, false);
            holder = new ViewHolder();
            holder.front = (RelativeLayout) itemView.findViewById(R.id.tile_front);
            holder.back = (RelativeLayout) itemView.findViewById(R.id.tile_back);
            holder.icon = (ImageView) itemView.findViewById(R.id.tile_icon);
            holder.label = (TextView) itemView.findViewById(R.id.tile_label);
            holder.backLabel = (TextView) itemView.findViewById(R.id.tile_back_label);
            itemView.setTag(holder);
        } else {
            itemView = convertView;
            holder = (ViewHolder) itemView.getTag();
        }

        holder.icon.setImageResource(icons[position]);
        holder.label.setText(titles[position]);
        if (holder.backLabel != null) {
            holder.backLabel.setText("正在打开 " + titles[position] + "...");
        }

        // 重置状态
        holder.front.setVisibility(View.VISIBLE);
        holder.front.setAlpha(1.0f);
        holder.back.setVisibility(View.VISIBLE);
        holder.back.setAlpha(0.0f);
        holder.backLabel.setRotationY(0f);
        holder.label.setRotationY(0f);
        itemView.setScaleX(1.0f);
        itemView.setScaleY(1.0f);
        itemView.setAlpha(1.0f);
        itemView.setRotationY(0f);
        itemView.setTranslationX(0f);
        itemView.setTranslationY(0f);
        itemView.clearAnimation();
        itemView.setEnabled(true);
        itemView.setVisibility(View.VISIBLE);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (listener == null || rootContainer == null) return;

                v.setEnabled(false);

                final ViewHolder h = (ViewHolder) v.getTag();
                final String label = titles[position];

                int screenW = rootContainer.getWidth();
                int screenH = rootContainer.getHeight();

                int[] location = new int[2];
                v.getLocationOnScreen(location);
                int tileW = v.getWidth();
                int tileH = v.getHeight();

                // 创建浮层
                final View flyingTile = LayoutInflater.from(context).inflate(R.layout.item_tv_tile, rootContainer, false);
                ImageView fIcon = (ImageView) flyingTile.findViewById(R.id.tile_icon);
                TextView fLabel = (TextView) flyingTile.findViewById(R.id.tile_label);
                final TextView fBackLabel = (TextView) flyingTile.findViewById(R.id.tile_back_label);
                final RelativeLayout fFront = (RelativeLayout) flyingTile.findViewById(R.id.tile_front);
                final RelativeLayout fBack = (RelativeLayout) flyingTile.findViewById(R.id.tile_back);

                fIcon.setImageResource(icons[position]);
                fLabel.setText(titles[position]);
                fBackLabel.setText("正在打开 " + titles[position] + "...");

                fFront.setVisibility(View.VISIBLE);
                fFront.setAlpha(1.0f);
                fBack.setVisibility(View.VISIBLE);
                fBack.setAlpha(0.0f);
                fBackLabel.setRotationY(0f);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(tileW, tileH);
                params.leftMargin = location[0];
                params.topMargin = location[1];
                rootContainer.addView(flyingTile, params);

                v.setVisibility(View.INVISIBLE);

                float centerX = location[0] + tileW / 2f;
                float centerY = location[1] + tileH / 2f;
                float screenCenterX = screenW / 2f;
                float screenCenterY = screenH / 2f;

                float transX = screenCenterX - centerX;
                float transY = screenCenterY - centerY;

                float scaleX = (float) screenW / tileW;
                float scaleY = (float) screenH / tileH;

                flyingTile.setPivotX(tileW / 2f);
                flyingTile.setPivotY(tileH / 2f);
                flyingTile.setRotationY(0f);

                final int duration = 350;

                // 第一段：0° → 80°
                flyingTile.animate()
                        .scaleX(scaleX)
                        .scaleY(scaleY)
                        .translationX(transX)
                        .translationY(transY)
                        .rotationY(75f)
                        .setDuration(duration)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                flyingTile.setRotationY(100f);
                                fFront.setAlpha(0.0f);
                                fBack.setAlpha(1.0f);
                                fBackLabel.setRotationY(180f);

                                // 第二段：100° → 180°
                                flyingTile.animate()
                                        .rotationY(180f)
                                        .setDuration(duration)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                fBackLabel.setRotationY(0f);
                                                rootContainer.removeView(flyingTile);
                                                v.setVisibility(View.VISIBLE);
                                                v.setEnabled(true);
                                                if (listener != null) {
                                                    listener.onTileClick(label);
                                                }
                                            }
                                        })
                                        .start();
                            }
                        })
                        .start();
            }
        });

        // 强制正方形
        itemView.post(new Runnable() {
            @Override
            public void run() {
                int width = itemView.getWidth();
                if (width > 0) {
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    params.height = (int)(width * 0.7);
                    itemView.setLayoutParams(params);
                }
            }
        });

        // 焦点放大效果
        itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100).start();
                } else {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                }
            }
        });

        return itemView;
    }

    static class ViewHolder {
        RelativeLayout front;
        RelativeLayout back;
        ImageView icon;
        TextView label;
        TextView backLabel;
    }
}