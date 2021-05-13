package me.ibore.utils

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2017/01/17
 * desc  : utils about fragment
</pre> *
 */
object FragmentUtils {

    private const val TYPE_ADD_FRAGMENT = 0x01
    private const val TYPE_SHOW_FRAGMENT = 0x01 shl 1
    private const val TYPE_HIDE_FRAGMENT = 0x01 shl 2
    private const val TYPE_SHOW_HIDE_FRAGMENT = 0x01 shl 3
    private const val TYPE_REPLACE_FRAGMENT = 0x01 shl 4
    private const val TYPE_REMOVE_FRAGMENT = 0x01 shl 5
    private const val TYPE_REMOVE_TO_FRAGMENT = 0x01 shl 6
    private const val ARGS_ID = "args_id"
    private const val ARGS_IS_HIDE = "args_is_hide"
    private const val ARGS_IS_ADD_STACK = "args_is_add_stack"
    private const val ARGS_TAG = "args_tag"

    /**
     * Add fragment.
     *
     * @param fm          The manager of fragment.
     * @param add         The fragment will be add.
     * @param containerId The id of container.
     * @param tag         The tag of fragment.
     * @param isHide      True to hide, false otherwise.
     * @param isAddStack  True to add fragment in stack, false otherwise.
     */
    @JvmStatic
    @JvmOverloads
    fun add(
        fm: FragmentManager, add: Fragment, @IdRes containerId: Int,
        tag: String? = null, isHide: Boolean = false, isAddStack: Boolean = false
    ) {
        putArgs(add, Args(containerId, tag, isHide, isAddStack))
        operateNoAnim(TYPE_ADD_FRAGMENT, fm, null, add)
    }

    /**
     * Add fragment.
     *
     * @param fm           The manager of fragment.
     * @param add          The fragment will be add.
     * @param containerId  The id of container.
     * @param tag          The tag of fragment.
     * @param isAddStack   True to add fragment in stack, false otherwise.
     * @param enterAnim    An animation or animator resource ID used for the enter animation on the
     * view of the fragment being added or attached.
     * @param exitAnim     An animation or animator resource ID used for the exit animation on the
     * view of the fragment being removed or detached.
     * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
     * view of the fragment being readded or reattached caused by
     * popBackStack() or similar methods.
     * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
     * view of the fragment being removed or detached caused by
     * popBackStack() or similar methods.
     */
    @JvmStatic
    @JvmOverloads
    fun add(
        fm: FragmentManager, add: Fragment, @IdRes containerId: Int,
        tag: String? = null, isAddStack: Boolean = false,
        @AnimatorRes @AnimRes enterAnim: Int,
        @AnimatorRes @AnimRes exitAnim: Int,
        @AnimatorRes @AnimRes popEnterAnim: Int = 0,
        @AnimatorRes @AnimRes popExitAnim: Int = 0
    ) {
        val ft = fm.beginTransaction()
        putArgs(add, Args(containerId, tag, false, isAddStack))
        addAnim(ft, enterAnim, exitAnim, popEnterAnim, popExitAnim)
        operate(TYPE_ADD_FRAGMENT, fm, ft, null, add)
    }


    /**
     * Add fragment.
     *
     * @param fm             The manager of fragment.
     * @param add            The fragment will be add.
     * @param containerId    The id of container.
     * @param isAddStack     True to add fragment in stack, false otherwise.
     * @param sharedElements A View in a disappearing Fragment to match with a View in an
     * appearing Fragment.
     */
    @JvmStatic
    @JvmOverloads
    fun add(
        fm: FragmentManager, add: Fragment, @IdRes containerId: Int, tag: String? = null,
        isAddStack: Boolean = false, vararg sharedElements: View
    ) {
        val ft = fm.beginTransaction()
        putArgs(add, Args(containerId, tag, false, isAddStack))
        addSharedElement(ft, *sharedElements)
        operate(TYPE_ADD_FRAGMENT, fm, ft, null, add)
    }

    /**
     * Add fragment.
     *
     * @param fm          The manager of fragment.
     * @param adds        The fragments will be add.
     * @param containerId The id of container.
     * @param showIndex   The index of fragment will be shown.
     */
    @JvmStatic
    @JvmOverloads
    fun add(
        fm: FragmentManager, adds: List<Fragment>, @IdRes containerId: Int,
        tags: Array<String?>? = null, showIndex: Int
    ) {
        if (tags == null) {
            var i = 0
            val len = adds.size
            while (i < len) {
                putArgs(adds[i], Args(containerId, null, showIndex != i, false))
                ++i
            }
        } else {
            var i = 0
            val len = adds.size
            while (i < len) {
                putArgs(adds[i], Args(containerId, tags[i], showIndex != i, false))
                ++i
            }
        }
        operateNoAnim(TYPE_ADD_FRAGMENT, fm, null, *adds.toTypedArray())
    }

    /**
     * Show fragment.
     *
     * @param show The fragment will be show.
     */
    fun show(show: Fragment) {
        putArgs(show, false)
        operateNoAnim(TYPE_SHOW_FRAGMENT, show.fragmentManager, null, show)
    }

    /**
     * Show fragment.
     *
     * @param fm The manager of fragment.
     */
    fun show(fm: FragmentManager) {
        val fragments = getFragments(fm)
        for (show in fragments) {
            putArgs(show, false)
        }
        operateNoAnim(TYPE_SHOW_FRAGMENT, fm, null, *fragments.toTypedArray())
    }

    /**
     * Hide fragment.
     *
     * @param hide The fragment will be hide.
     */
    fun hide(hide: Fragment) {
        putArgs(hide, true)
        operateNoAnim(TYPE_HIDE_FRAGMENT, hide.fragmentManager, null, hide)
    }

    /**
     * Hide fragment.
     *
     * @param fm The manager of fragment.
     */
    fun hide(fm: FragmentManager) {
        val fragments = getFragments(fm)
        for (hide in fragments) {
            putArgs(hide, true)
        }
        operateNoAnim(TYPE_HIDE_FRAGMENT, fm, null, *fragments.toTypedArray())
    }

    /**
     * Show fragment then hide other fragment.
     *
     * @param show The fragment will be show.
     * @param hide The fragment will be hide.
     */
    fun showHide(show: Fragment, hide: Fragment) {
        showHide(show, listOf(hide))
    }

    /**
     * Show fragment then hide other fragment.
     *
     * @param showIndex The index of fragment will be shown.
     * @param fragments The fragment will be hide.
     */
    fun showHide(showIndex: Int, vararg fragments: Fragment) {
        showHide(fragments[showIndex], *fragments)
    }

    /**
     * Show fragment then hide other fragment.
     *
     * @param show The fragment will be show.
     * @param hide The fragment will be hide.
     */
    fun showHide(show: Fragment, vararg hide: Fragment) {
        showHide(show, listOf(*hide))
    }

    /**
     * Show fragment then hide other fragment.
     *
     * @param showIndex The index of fragment will be shown.
     * @param fragments The fragments will be hide.
     */
    fun showHide(showIndex: Int, fragments: List<Fragment>) {
        showHide(fragments[showIndex], fragments)
    }

    /**
     * Show fragment then hide other fragment.
     *
     * @param show The fragment will be show.
     * @param hide The fragment will be hide.
     */
    fun showHide(show: Fragment, hide: List<Fragment>) {
        for (fragment in hide) {
            putArgs(fragment, fragment !== show)
        }
        operateNoAnim(TYPE_SHOW_HIDE_FRAGMENT, show.fragmentManager, show, *hide.toTypedArray())
    }

    /**
     * Show fragment then hide other fragment.
     *
     * @param show The fragment will be show.
     * @param hide The fragment will be hide.
     */
    fun showHide(
        show: Fragment, hide: Fragment,
        @AnimatorRes @AnimRes enterAnim: Int,
        @AnimatorRes @AnimRes exitAnim: Int,
        @AnimatorRes @AnimRes popEnterAnim: Int,
        @AnimatorRes @AnimRes popExitAnim: Int
    ) {
        showHide(show, listOf(hide), enterAnim, exitAnim, popEnterAnim, popExitAnim)
    }

    /**
     * Show fragment then hide other fragment.
     *
     * @param showIndex The index of fragment will be shown.
     * @param fragments The fragments will be hide.
     */
    fun showHide(
        showIndex: Int, fragments: List<Fragment>,
        @AnimatorRes @AnimRes enterAnim: Int, @AnimatorRes @AnimRes exitAnim: Int,
        @AnimatorRes @AnimRes popEnterAnim: Int, @AnimatorRes @AnimRes popExitAnim: Int
    ) {
        showHide(fragments[showIndex], fragments, enterAnim, exitAnim, popEnterAnim, popExitAnim)
    }

    /**
     * Show fragment then hide other fragment.
     *
     * @param show The fragment will be show.
     * @param hide The fragment will be hide.
     */
    fun showHide(
        show: Fragment, hide: List<Fragment>,
        @AnimatorRes @AnimRes enterAnim: Int, @AnimatorRes @AnimRes exitAnim: Int,
        @AnimatorRes @AnimRes popEnterAnim: Int, @AnimatorRes @AnimRes popExitAnim: Int
    ) {
        for (fragment in hide) {
            putArgs(fragment, fragment !== show)
        }
        val fm = show.fragmentManager
        if (fm != null) {
            val ft = fm.beginTransaction()
            addAnim(ft, enterAnim, exitAnim, popEnterAnim, popExitAnim)
            operate(TYPE_SHOW_HIDE_FRAGMENT, fm, ft, show, *hide.toTypedArray())
        }
    }

    /**
     * Replace fragment.
     *
     * @param srcFragment  The source of fragment.
     * @param destFragment The destination of fragment.
     * @param destTag      The destination of fragment's tag.
     * @param isAddStack   True to add fragment in stack, false otherwise.
     * @param enterAnim    An animation or animator resource ID used for the enter animation on the
     * view of the fragment being added or attached.
     * @param exitAnim     An animation or animator resource ID used for the exit animation on the
     * view of the fragment being removed or detached.
     * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
     * view of the fragment being readded or reattached caused by
     * popBackStack() or similar methods.
     * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
     * view of the fragment being removed or detached caused by
     * popBackStack() or similar methods.
     */
    @JvmOverloads
    fun replace(
        srcFragment: Fragment,
        destFragment: Fragment,
        destTag: String? = null,
        isAddStack: Boolean = false,
        @AnimatorRes @AnimRes enterAnim: Int = 0,
        @AnimatorRes @AnimRes exitAnim: Int = 0,
        @AnimatorRes @AnimRes popEnterAnim: Int = 0,
        @AnimatorRes @AnimRes popExitAnim: Int = 0
    ) {
        val fm = srcFragment.fragmentManager ?: return
        val args = getArgs(srcFragment)
        replace(
            fm, destFragment, args.id, destTag, isAddStack,
            enterAnim, exitAnim, popEnterAnim, popExitAnim
        )
    }

    /**
     * Replace fragment.
     *
     * @param srcFragment    The source of fragment.
     * @param destFragment   The destination of fragment.
     * @param destTag        The destination of fragment's tag.
     * @param isAddStack     True to add fragment in stack, false otherwise.
     * @param sharedElements A View in a disappearing Fragment to match with a View in an
     * appearing Fragment.
     */
    @JvmOverloads
    fun replace(
        srcFragment: Fragment,
        destFragment: Fragment,
        destTag: String? = null,
        isAddStack: Boolean= false,
        vararg sharedElements: View
    ) {
        val fm = srcFragment.fragmentManager ?: return
        val args = getArgs(srcFragment)
        replace(
            fm,
            destFragment,
            args.id,
            destTag,
            isAddStack,
            *sharedElements
        )
    }

    /**
     * Replace fragment.
     *
     * @param fm           The manager of fragment.
     * @param fragment     The new fragment to place in the container.
     * @param containerId  The id of container.
     * @param destTag      The destination of fragment's tag.
     * @param isAddStack   True to add fragment in stack, false otherwise.
     * @param enterAnim    An animation or animator resource ID used for the enter animation on the
     * view of the fragment being added or attached.
     * @param exitAnim     An animation or animator resource ID used for the exit animation on the
     * view of the fragment being removed or detached.
     * @param popEnterAnim An animation or animator resource ID used for the enter animation on the
     * view of the fragment being readded or reattached caused by
     * popBackStack() or similar methods.
     * @param popExitAnim  An animation or animator resource ID used for the enter animation on the
     * view of the fragment being removed or detached caused by
     * popBackStack() or similar methods.
     */
    @JvmOverloads
    @JvmStatic
    fun replace(
        fm: FragmentManager,
        fragment: Fragment,
        @IdRes containerId: Int,
        destTag: String? = null,
        isAddStack: Boolean = false,
        @AnimatorRes @AnimRes enterAnim: Int = 0,
        @AnimatorRes @AnimRes exitAnim: Int = 0,
        @AnimatorRes @AnimRes popEnterAnim: Int = 0,
        @AnimatorRes @AnimRes popExitAnim: Int = 0
    ) {
        val ft = fm.beginTransaction()
        putArgs(fragment, Args(containerId, destTag, false, isAddStack))
        addAnim(ft, enterAnim, exitAnim, popEnterAnim, popExitAnim)
        operate(TYPE_REPLACE_FRAGMENT, fm, ft, null, fragment)
    }



    /**
     * Replace fragment.
     *
     * @param fm             The manager of fragment.
     * @param fragment       The new fragment to place in the container.
     * @param containerId    The id of container.
     * @param destTag        The destination of fragment's tag.
     * @param isAddStack     True to add fragment in stack, false otherwise.
     * @param sharedElements A View in a disappearing Fragment to match with a View in an
     * appearing Fragment.
     */
    @JvmOverloads
    @JvmStatic
    fun replace(
        fm: FragmentManager,
        fragment: Fragment,
        @IdRes containerId: Int,
        destTag: String? = null,
        isAddStack: Boolean = false,
        vararg sharedElements: View
    ) {
        val ft = fm.beginTransaction()
        putArgs(fragment, Args(containerId, destTag, false, isAddStack))
        addSharedElement(ft, *sharedElements)
        operate(TYPE_REPLACE_FRAGMENT, fm, ft, null, fragment)
    }
    /**
     * Pop fragment.
     *
     * @param fm          The manager of fragment.
     * @param isImmediate True to pop immediately, false otherwise.
     */
    @JvmOverloads
    fun pop(
        fm: FragmentManager,
        isImmediate: Boolean = true
    ) {
        if (isImmediate) {
            fm.popBackStackImmediate()
        } else {
            fm.popBackStack()
        }
    }
    /**
     * Pop to fragment.
     *
     * @param fm            The manager of fragment.
     * @param popClz        The class of fragment will be popped to.
     * @param isIncludeSelf True to include the fragment, false otherwise.
     * @param isImmediate   True to pop immediately, false otherwise.
     */
    @JvmOverloads
    fun popTo(
        fm: FragmentManager,
        popClz: Class<out Fragment?>,
        isIncludeSelf: Boolean,
        isImmediate: Boolean = true
    ) {
        if (isImmediate) {
            fm.popBackStackImmediate(
                popClz.name,
                if (isIncludeSelf) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0
            )
        } else {
            fm.popBackStack(
                popClz.name,
                if (isIncludeSelf) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0
            )
        }
    }
    /**
     * Pop all fragments.
     *
     * @param fm The manager of fragment.
     */
    @JvmOverloads
    fun popAll(fm: FragmentManager, isImmediate: Boolean = true) {
        if (fm.backStackEntryCount > 0) {
            val entry = fm.getBackStackEntryAt(0)
            if (isImmediate) {
                fm.popBackStackImmediate(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            } else {
                fm.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    /**
     * Remove fragment.
     *
     * @param remove The fragment will be removed.
     */
    fun remove(remove: Fragment) {
        operateNoAnim(TYPE_REMOVE_FRAGMENT, remove.fragmentManager, null, remove)
    }

    /**
     * Remove to fragment.
     *
     * @param removeTo      The fragment will be removed to.
     * @param isIncludeSelf True to include the fragment, false otherwise.
     */
    fun removeTo(removeTo: Fragment, isIncludeSelf: Boolean) {
        operateNoAnim(
            TYPE_REMOVE_TO_FRAGMENT,
            removeTo.fragmentManager,
            if (isIncludeSelf) removeTo else null,
            removeTo
        )
    }

    /**
     * Remove all fragments.
     *
     * @param fm The manager of fragment.
     */
    fun removeAll(fm: FragmentManager) {
        val fragments = getFragments(fm)
        operateNoAnim(TYPE_REMOVE_FRAGMENT, fm, null, *fragments.toTypedArray())
    }

    private fun putArgs(fragment: Fragment, args: Args) {
        var bundle = fragment.arguments
        if (bundle == null) {
            bundle = Bundle()
            fragment.arguments = bundle
        }
        bundle.putInt(ARGS_ID, args.id)
        bundle.putBoolean(ARGS_IS_HIDE, args.isHide)
        bundle.putBoolean(ARGS_IS_ADD_STACK, args.isAddStack)
        bundle.putString(ARGS_TAG, args.tag)
    }

    private fun putArgs(fragment: Fragment, isHide: Boolean) {
        var bundle = fragment.arguments
        if (bundle == null) {
            bundle = Bundle()
            fragment.arguments = bundle
        }
        bundle.putBoolean(ARGS_IS_HIDE, isHide)
    }

    private fun getArgs(fragment: Fragment): Args {
        var bundle = fragment.arguments
        if (bundle == null) bundle = Bundle.EMPTY
        return Args(
            bundle!!.getInt(ARGS_ID, fragment.id),
            bundle.getBoolean(ARGS_IS_HIDE),
            bundle.getBoolean(ARGS_IS_ADD_STACK)
        )
    }

    private fun operateNoAnim(
        type: Int, fm: FragmentManager?,
        src: Fragment?,
        vararg dest: Fragment
    ) {
        if (fm == null) return
        val ft = fm.beginTransaction()
        operate(type, fm, ft, src, *dest)
    }

    private fun operate(
        type: Int,
        fm: FragmentManager,
        ft: FragmentTransaction,
        src: Fragment?,
        vararg dest: Fragment
    ) {
        if (src != null && src.isRemoving) {
            Log.e("FragmentUtils", src.javaClass.name + " is isRemoving")
            return
        }
        var name: String
        var args: Bundle?
        when (type) {
            TYPE_ADD_FRAGMENT -> for (fragment in dest) {
                args = fragment.arguments
                if (args == null) return
                name = args.getString(ARGS_TAG, fragment.javaClass.name)
                val fragmentByTag = fm.findFragmentByTag(name)
                if (fragmentByTag != null && fragmentByTag.isAdded) {
                    ft.remove(fragmentByTag)
                }
                ft.add(args.getInt(ARGS_ID), fragment, name)
                if (args.getBoolean(ARGS_IS_HIDE)) ft.hide(fragment)
                if (args.getBoolean(ARGS_IS_ADD_STACK)) ft.addToBackStack(name)
            }
            TYPE_HIDE_FRAGMENT -> for (fragment in dest) {
                ft.hide(fragment)
            }
            TYPE_SHOW_FRAGMENT -> for (fragment in dest) {
                ft.show(fragment)
            }
            TYPE_SHOW_HIDE_FRAGMENT -> {
                ft.show(src!!)
                for (fragment in dest) {
                    if (fragment !== src) {
                        ft.hide(fragment)
                    }
                }
            }
            TYPE_REPLACE_FRAGMENT -> {
                args = dest[0].arguments
                if (args == null) return
                name = args.getString(ARGS_TAG, dest[0].javaClass.name)
                ft.replace(args.getInt(ARGS_ID), dest[0], name)
                if (args.getBoolean(ARGS_IS_ADD_STACK)) ft.addToBackStack(name)
            }
            TYPE_REMOVE_FRAGMENT -> for (fragment in dest) {
                if (fragment !== src) {
                    ft.remove(fragment)
                }
            }
            TYPE_REMOVE_TO_FRAGMENT -> {
                var i = dest.size - 1
                while (i >= 0) {
                    val fragment = dest[i]
                    if (fragment === dest[0]) {
                        if (src != null) ft.remove(fragment)
                        break
                    }
                    ft.remove(fragment)
                    --i
                }
            }
        }
        ft.commitAllowingStateLoss()
    }

    private fun addAnim(
        ft: FragmentTransaction,
        enter: Int,
        exit: Int,
        popEnter: Int,
        popExit: Int
    ) {
        ft.setCustomAnimations(enter, exit, popEnter, popExit)
    }

    private fun addSharedElement(
        ft: FragmentTransaction,
        vararg views: View
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (view in views) {
                ft.addSharedElement(view, view.transitionName)
            }
        }
    }

    /**
     * Return the top fragment.
     *
     * @param fm The manager of fragment.
     * @return the top fragment
     */
    fun getTop(fm: FragmentManager): Fragment? {
        return getTopIsInStack(fm, null, false)
    }

    /**
     * Return the top fragment in stack.
     *
     * @param fm The manager of fragment.
     * @return the top fragment in stack
     */
    fun getTopInStack(fm: FragmentManager): Fragment? {
        return getTopIsInStack(fm, null, true)
    }

    private fun getTopIsInStack(
        fm: FragmentManager,
        parentFragment: Fragment?,
        isInStack: Boolean
    ): Fragment? {
        val fragments = getFragments(fm)
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null) {
                if (isInStack) {
                    val args = fragment.arguments
                    if (args != null && args.getBoolean(ARGS_IS_ADD_STACK)) {
                        return getTopIsInStack(fragment.childFragmentManager, fragment, true)
                    }
                } else {
                    return getTopIsInStack(fragment.childFragmentManager, fragment, false)
                }
            }
        }
        return parentFragment
    }

    /**
     * Return the top fragment which is shown.
     *
     * @param fm The manager of fragment.
     * @return the top fragment which is shown
     */
    fun getTopShow(fm: FragmentManager): Fragment? {
        return getTopShowIsInStack(fm, null, false)
    }

    /**
     * Return the top fragment which is shown in stack.
     *
     * @param fm The manager of fragment.
     * @return the top fragment which is shown in stack
     */
    fun getTopShowInStack(fm: FragmentManager): Fragment? {
        return getTopShowIsInStack(fm, null, true)
    }

    private fun getTopShowIsInStack(
        fm: FragmentManager,
        parentFragment: Fragment?,
        isInStack: Boolean
    ): Fragment? {
        val fragments = getFragments(fm)
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment != null && fragment.isResumed
                && fragment.isVisible
                && fragment.userVisibleHint
            ) {
                if (isInStack) {
                    val args = fragment.arguments
                    if (args != null && args.getBoolean(ARGS_IS_ADD_STACK)) {
                        return getTopShowIsInStack(fragment.childFragmentManager, fragment, true)
                    }
                } else {
                    return getTopShowIsInStack(fragment.childFragmentManager, fragment, false)
                }
            }
        }
        return parentFragment
    }

    /**
     * Return the fragments in manager.
     *
     * @param fm The manager of fragment.
     * @return the fragments in manager
     */
    fun getFragments(fm: FragmentManager): List<Fragment> {
        val fragments = fm.fragments
        return if (fragments.isEmpty()) emptyList() else fragments
    }

    /**
     * Return the fragments in stack in manager.
     *
     * @param fm The manager of fragment.
     * @return the fragments in stack in manager
     */
    fun getFragmentsInStack(fm: FragmentManager): List<Fragment> {
        val fragments = getFragments(fm)
        val result: MutableList<Fragment> = ArrayList()
        for (fragment in fragments) {
            val args = fragment.arguments
            if (args != null && args.getBoolean(ARGS_IS_ADD_STACK)) {
                result.add(fragment)
            }
        }
        return result
    }

    /**
     * Return all fragments in manager.
     *
     * @param fm The manager of fragment.
     * @return all fragments in manager
     */
    fun getAllFragments(fm: FragmentManager): List<FragmentNode> {
        return getAllFragments(fm, ArrayList())
    }

    private fun getAllFragments(
        fm: FragmentManager,
        result: MutableList<FragmentNode>
    ): List<FragmentNode> {
        val fragments = getFragments(fm)
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            result.add(
                FragmentNode(
                    fragment,
                    getAllFragments(
                        fragment.childFragmentManager,
                        ArrayList()
                    )
                )
            )
        }
        return result
    }

    /**
     * Return all fragments in stack in manager.
     *
     * @param fm The manager of fragment.
     * @return all fragments in stack in manager
     */
    fun getAllFragmentsInStack(fm: FragmentManager): List<FragmentNode> {
        return getAllFragmentsInStack(fm, ArrayList())
    }

    private fun getAllFragmentsInStack(
        fm: FragmentManager,
        result: MutableList<FragmentNode>
    ): List<FragmentNode> {
        val fragments = getFragments(fm)
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            val args = fragment.arguments
            if (args != null && args.getBoolean(ARGS_IS_ADD_STACK)) {
                result.add(
                    FragmentNode(
                        fragment,
                        getAllFragmentsInStack(
                            fragment.childFragmentManager,
                            ArrayList()
                        )
                    )
                )
            }
        }
        return result
    }

    /**
     * Find fragment.
     *
     * @param fm      The manager of fragment.
     * @param findClz The class of fragment will be found.
     * @return the fragment matches class
     */
    fun findFragment(
        fm: FragmentManager,
        findClz: Class<out Fragment?>
    ): Fragment? {
        return fm.findFragmentByTag(findClz.name)
    }

    /**
     * Find fragment.
     *
     * @param fm  The manager of fragment.
     * @param tag The tag of fragment will be found.
     * @return the fragment matches class
     */
    fun findFragment(
        fm: FragmentManager,
        tag: String
    ): Fragment? {
        return fm.findFragmentByTag(tag)
    }

    /**
     * Dispatch the back press for fragment.
     *
     * @param fragment The fragment.
     * @return `true`: the fragment consumes the back press<br></br>`false`: otherwise
     */
    fun dispatchBackPress(fragment: Fragment): Boolean {
        return (fragment.isResumed
                && fragment.isVisible
                && fragment.userVisibleHint
                && fragment is OnBackClickListener
                && (fragment as OnBackClickListener).onBackClick())
    }

    /**
     * Dispatch the back press for fragment.
     *
     * @param fm The manager of fragment.
     * @return `true`: the fragment consumes the back press<br></br>`false`: otherwise
     */
    fun dispatchBackPress(fm: FragmentManager): Boolean {
        val fragments = getFragments(fm)
        if (fragments.isEmpty()) return false
        for (i in fragments.indices.reversed()) {
            val fragment = fragments[i]
            if (fragment.isResumed && fragment.isVisible && fragment.userVisibleHint
                && fragment is OnBackClickListener && (fragment as OnBackClickListener).onBackClick()
            ) {
                return true
            }
        }
        return false
    }

    /**
     * Set background color for fragment.
     *
     * @param fragment The fragment.
     * @param color    The background color.
     */
    fun setBackgroundColor(
        fragment: Fragment,
        @ColorInt color: Int
    ) {
        val view = fragment.view
        view?.setBackgroundColor(color)
    }

    /**
     * Set background resource for fragment.
     *
     * @param fragment The fragment.
     * @param resId    The resource id.
     */
    fun setBackgroundResource(
        fragment: Fragment,
        @DrawableRes resId: Int
    ) {
        val view = fragment.view
        view?.setBackgroundResource(resId)
    }

    /**
     * Set background color for fragment.
     *
     * @param fragment   The fragment.
     * @param background The background.
     */
    fun setBackground(fragment: Fragment, background: Drawable?) {
        val view = fragment.view ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.background = background
        } else {
            view.setBackgroundDrawable(background)
        }
    }

    /**
     * Return the simple name of fragment.
     *
     * @param fragment The fragment.
     * @return the simple name of fragment
     */
    fun getSimpleName(fragment: Fragment?): String {
        return if (fragment == null) "null" else fragment.javaClass.simpleName
    }

    private class Args internal constructor(
        val id: Int, val tag: String?,
        val isHide: Boolean, val isAddStack: Boolean
    ) {
        internal constructor(id: Int, isHide: Boolean, isAddStack: Boolean) : this(
            id,
            null,
            isHide,
            isAddStack
        ) {
        }
    }

    class FragmentNode(val fragment: Fragment, val next: List<FragmentNode>?) {
        override fun toString(): String {
            return (fragment.javaClass.simpleName
                    + "->"
                    + if (next == null || next.isEmpty()) "no child" else next.toString())
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    interface OnBackClickListener {
        fun onBackClick(): Boolean
    }
}