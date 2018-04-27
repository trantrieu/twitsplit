package com.zalora.twitsplit.matcher

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


/**
 * Created by Apple on 1/18/18.
 */
class RecyclerViewMatcher(private val recyclerViewId: Int) {

    companion object {
        fun withRecyclerView(recyclerViewId: Int) = RecyclerViewMatcher(recyclerViewId)
    }

    fun atPositionOnView(position: Int, targetViewId: Int = -1, adapterClass: Class<*>?): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            internal var resources: Resources? = null
            internal var childView: View? = null

            override fun describeTo(description: Description) {
                var idDescription = Integer.toString(recyclerViewId)
                if (this.resources != null) {
                    idDescription = try {
                        this.resources!!.getResourceName(recyclerViewId)
                    } catch (var4: Resources.NotFoundException) {
                        String.format("%s (resource name not found)", recyclerViewId)
                    }
                }

                description.appendText("RecyclerView with id: $idDescription at position: $position")
            }

            override fun matchesSafely(view: View): Boolean {

                this.resources = view.resources

                /**
                 * Case no need type of class
                 */
                if (childView == null && adapterClass == null) {
                    val recyclerView = view.rootView.findViewById(recyclerViewId) as RecyclerView
                    if (recyclerView.id == recyclerViewId) {
                        childView = recyclerView.findViewHolderForAdapterPosition(position).itemView
                    }
                }

                /**
                 * Case need type of class
                 */
                if (childView == null && adapterClass != null) {
                    val recyclerView = findRecyclerViewById(view.rootView as ViewGroup, recyclerViewId, adapterClass) as? RecyclerView
                    if (recyclerView != null) {
                        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                        if (viewHolder != null) {
                            childView = viewHolder.itemView
                        }
                    }
                }

                if (childView == null) {
                    return false
                }

                return if (targetViewId == -1) {
                    view == childView
                } else {
                    val targetView = childView!!.findViewById<View>(targetViewId)
                    view == targetView
                }
            }
        }
    }

    private fun findRecyclerViewById(viewGroup: ViewGroup, id: Int, clazz: Class<*>): View? {
        var findView: View? = null
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            if (view.id == id && view is RecyclerView && clazz.isInstance(view.adapter)) {
                findView = view
                break
            } else if (view is ViewGroup) {
                findView = findRecyclerViewById(view, id, clazz)
                if (findView != null) {
                    break
                }
            }
        }
        return findView
    }
}