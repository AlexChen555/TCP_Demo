// Generated code from Butter Knife. Do not modify!
package com.dream.tcp;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity3$$ViewBinder<T extends com.dream.tcp.MainActivity3> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492946, "field 'mBtn_start' and method 'onClick'");
    target.mBtn_start = finder.castView(view, 2131492946, "field 'mBtn_start'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492949, "field 'mBtn_request' and method 'onClick'");
    target.mBtn_request = finder.castView(view, 2131492949, "field 'mBtn_request'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492950, "field 'mBtn_stop' and method 'onClick'");
    target.mBtn_stop = finder.castView(view, 2131492950, "field 'mBtn_stop'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492951, "field 'mBtn_03' and method 'onClick'");
    target.mBtn_03 = finder.castView(view, 2131492951, "field 'mBtn_03'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492944, "field 'mEdClientIp'");
    target.mEdClientIp = finder.castView(view, 2131492944, "field 'mEdClientIp'");
    view = finder.findRequiredView(source, 2131492945, "field 'mEdClientPort'");
    target.mEdClientPort = finder.castView(view, 2131492945, "field 'mEdClientPort'");
    view = finder.findRequiredView(source, 2131492947, "field 'mEdClientSendMessage'");
    target.mEdClientSendMessage = finder.castView(view, 2131492947, "field 'mEdClientSendMessage'");
    view = finder.findRequiredView(source, 2131492953, "field 'mLvClientMsg'");
    target.mLvClientMsg = finder.castView(view, 2131492953, "field 'mLvClientMsg'");
    view = finder.findRequiredView(source, 2131492948, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492952, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.mBtn_start = null;
    target.mBtn_request = null;
    target.mBtn_stop = null;
    target.mBtn_03 = null;
    target.mEdClientIp = null;
    target.mEdClientPort = null;
    target.mEdClientSendMessage = null;
    target.mLvClientMsg = null;
  }
}
