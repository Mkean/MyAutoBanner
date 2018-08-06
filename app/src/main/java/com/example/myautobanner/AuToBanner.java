package com.example.myautobanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * author:王庆
 * date：On 2018/8/2
 */
public class AuToBanner extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private final int DELAY_TIME = 4000;//自动轮播时间
    private List<Integer> mUrls;
    private List<ImageView> mViewpagerViews;
    private List<ImageView> mDotImageviews;
    private Context context;
    private int size;//圆点的大小
    private int margin;//圆点的间距
    private int count;//viewpager中view的数量
    private ViewPager mViewPager;
    private LinearLayout mDotlayout;//圆点布局
    private int currentItem;//当前viewpager索引
    private Handler handler = new Handler();
    private BannerClicklistener mBannerClicklistener;
    private MyPager myPager;

    public AuToBanner(Context context) {
        this(context, null);
    }

    public AuToBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AuToBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化数据
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        mUrls = new ArrayList<>();
        mViewpagerViews = new ArrayList<>();
        mDotImageviews = new ArrayList<>();
        //拿到自定义的属性数组
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        //得到数组里的自定义的size（圆点大小）
        size = typedArray.getDimensionPixelSize(R.styleable.Banner_size, 10);
        //得到数组里的自定义的margin（圆点间距）
        margin = typedArray.getDimensionPixelSize(R.styleable.Banner_margin, 10);
        typedArray.recycle();//通知jvm的垃圾回收器，当你回收对象的时候，一定要把我回收了
        View view = LayoutInflater.from(context).inflate(R.layout.auto_banner_view, this, true);
        mViewPager = view.findViewById(R.id.viewpager);
        mDotlayout = view.findViewById(R.id.layout_dot);
        //添加viewpager页面改变监听
        mViewPager.addOnPageChangeListener(this);
    }

    /**
     * 绘制自定义view的所有元素
     */
    public void display() {
        //绘制viewpager
        drawViewpager();
        //绘制圆点
        drawDots();
        //设置自动滚动
        setAuto();
    }

    /**
     * 设置自动滚动
     */
    private void setAuto() {
        handler.postDelayed(mTask, DELAY_TIME);
    }

    /**
     * 定时任务
     */
    Runnable mTask = new Runnable() {
        @Override
        public void run() {
            currentItem++;
            if (currentItem >= count) {
                currentItem = 0;
            }
            mViewPager.setCurrentItem(currentItem);
            handler.postDelayed(this, DELAY_TIME);
        }
    };

    /**
     * 传urls
     *
     * @param urls
     */
    public AuToBanner loadData(List<Integer> urls) {
        this.mUrls = urls;
        this.count = urls.size();
        return this;
    }

    /**
     * 绘制圆点
     */
    private void drawDots() {
        for (int i = 0; i < count; i++) {
            ImageView iv = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.leftMargin = margin;
            params.rightMargin = margin;
            iv.setLayoutParams(params);
            mDotImageviews.add(iv);
            if (i == 0) {
                iv.setImageResource(R.drawable.dot_selected);
            } else {
                iv.setImageResource(R.drawable.dot_normal);
            }
            mDotlayout.addView(iv);
        }


    }

    private void drawViewpager() {
        for (int i = 0; i < count; i++) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mViewpagerViews.add(iv);
        }
        if (mViewpagerViews != null) {
            myPager = new MyPager();
            mViewPager.setAdapter(myPager);
        }
    }

    class MyPager extends PagerAdapter {

        @Override
        public int getCount() {
            return mViewpagerViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            ImageView view = mViewpagerViews.get(position);
            Glide.with(context).load(mUrls.get(position)).into(view);

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //实现点击逻辑
                    mBannerClicklistener.onClickListener(position);
                }
            });
//            ImageLoader.getInstance().displayImage(mUrls.get(position), view);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 取消自动轮播任务
     */
    private void stopAuto() {
        handler.removeCallbacks(mTask);//取消任务
    }

    /**
     * 供外部调用者调用的接口类
     */
    public interface BannerClicklistener {
        void onClickListener(int pos);
    }

    /**
     * 供外部调用者初始化接口对象
     */
    public void setBannerClicklistener(BannerClicklistener bannerClicklistener) {
        this.mBannerClicklistener = bannerClicklistener;
    }

    //取消轮播任务
    public void cancel() {
        handler.removeCallbacks(mTask);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    /**
     * 切换页面的监听
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < count; i++) {
            if (i == position) {
                mDotImageviews.get(i).setImageResource(R.drawable.dot_selected);
            } else {
                mDotImageviews.get(i).setImageResource(R.drawable.dot_normal);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
