package com.trdz.day_picture.w_view.segment_book;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trdz.day_picture.R;


@SuppressWarnings("unused")
public class WindowKnowledgeBehavior extends CoordinatorLayout.Behavior<FloatingActionButton>
	{

	private final static float MIN_AVATAR_PERCENTAGE_SIZE = 0.3f;
	private final static int EXTRA_FINAL_AVATAR_PADDING = 80;

	private final static String TAG = "behavior";
	private final Context mContext;

	private float mCustomFinalYPosition;
	private float mCustomStartXPosition;
	private float mCustomStartToolbarPosition;
	private float mCustomStartHeight;
	private float mCustomFinalHeight;

	private float mAvatarMaxSize;
	private final float mFinalLeftAvatarPadding = 16;
	private float mStartPosition;
	private int mStartXPosition;
	private float mStartToolbarPosition;
	private int mStartYPosition;
	private int mFinalYPosition;
	private int mStartHeight;
	private int mFinalXPosition;
	private float mChangeBehaviorPoint;

	public WindowKnowledgeBehavior(Context context, AttributeSet attrs) {

		mContext = context;

		if (attrs != null)
			{
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageBehavior);
			mCustomFinalYPosition = a.getDimension(R.styleable.AvatarImageBehavior_finalYPosition, 0);
			mCustomStartXPosition = a.getDimension(R.styleable.AvatarImageBehavior_startXPosition, 0);
			mCustomStartToolbarPosition = a.getDimension(R.styleable.AvatarImageBehavior_startToolbarPosition, 0);
			mCustomStartHeight = a.getDimension(R.styleable.AvatarImageBehavior_startHeight, 0);
			mCustomFinalHeight = a.getDimension(R.styleable.AvatarImageBehavior_finalHeight, 0);

			a.recycle();
			}

		init();

		}

	private void init()
		{
		bindDimensions();
		}

	private void bindDimensions()
		{
		mAvatarMaxSize = mContext.getResources().getDimension(R.dimen.image_width);
		}

	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency)
		{
		return dependency instanceof Toolbar;
		}

	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency)
		{
		maybeInitProperties(child, dependency);

		final int maxScrollDistance = (int) (mStartToolbarPosition);
		float expandedPercentageFactor = dependency.getY() / maxScrollDistance;

		if (expandedPercentageFactor < mChangeBehaviorPoint)
			{
			float heightFactor = (mChangeBehaviorPoint - expandedPercentageFactor) / mChangeBehaviorPoint;

			float distanceXToSubtract = ((mStartXPosition - mFinalXPosition)
					* heightFactor) + (child.getHeight() / 2);
			float distanceYToSubtract = ((mStartYPosition - mFinalYPosition)
					* (1f - expandedPercentageFactor)) + (child.getHeight() / 2);

			child.setX(mStartXPosition - distanceXToSubtract);
			child.setY(mStartYPosition - distanceYToSubtract);

			float heightToSubtract = ((mStartHeight - mCustomFinalHeight) * heightFactor);

			CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
			lp.width = (int) (mStartHeight - heightToSubtract);
			lp.height = (int) (mStartHeight - heightToSubtract);
			child.setLayoutParams(lp);
			} else
			{
			float distanceYToSubtract = ((mStartYPosition - mFinalYPosition)
					* (1f - expandedPercentageFactor)) + (mStartHeight / 2);

			child.setX(mStartXPosition - child.getWidth() / 2);
			child.setY(mStartYPosition - distanceYToSubtract);

			CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
			lp.width = mStartHeight;
			lp.height = mStartHeight;
			child.setLayoutParams(lp);
			}
		return true;
		}

	private void maybeInitProperties(FloatingActionButton child, View dependency)
		{
		if (mStartYPosition == 0)
			mStartYPosition = (int) (dependency.getY());

		if (mFinalYPosition == 0)
			mFinalYPosition = (dependency.getHeight() / 2);

		if (mStartHeight == 0)
			mStartHeight = child.getHeight();

		if (mStartXPosition == 0)
			mStartXPosition = (int) (child.getX() + (child.getWidth() / 2));

		if (mFinalXPosition == 0)
			mFinalXPosition = mContext.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) + ((int) mCustomFinalHeight / 2);

		if (mStartToolbarPosition == 0)
			mStartToolbarPosition = dependency.getY();

		if (mChangeBehaviorPoint == 0)
			{
			mChangeBehaviorPoint = (child.getHeight() - mCustomFinalHeight) / (2f * (mStartYPosition - mFinalYPosition));
			}
		}

	public int getStatusBarHeight()
		{
		int result = 0;
		int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");

		if (resourceId > 0)
			{
			result = mContext.getResources().getDimensionPixelSize(resourceId);
			}
		return result;
		}
	}
