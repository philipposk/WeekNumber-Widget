package com.weeknumber.widget.widget

import com.weeknumber.widget.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetSizingTest {

    @Test
    fun `pickLayout compact for tiny widget`() {
        val s = WidgetSizing.Size(80, 80)
        assertEquals(R.layout.widget_home_screen_compact, WidgetSizing.pickLayout(s))
    }

    @Test
    fun `pickLayout small for 2x1`() {
        val s = WidgetSizing.Size(120, 80)
        assertEquals(R.layout.widget_home_screen_small, WidgetSizing.pickLayout(s))
    }

    @Test
    fun `pickLayout medium for default`() {
        val s = WidgetSizing.Size(180, 180)
        assertEquals(R.layout.widget_home_screen, WidgetSizing.pickLayout(s))
    }

    @Test
    fun `pickLayout large above 200`() {
        val s = WidgetSizing.Size(250, 250)
        assertEquals(R.layout.widget_home_screen_large, WidgetSizing.pickLayout(s))
    }

    @Test
    fun `pickLayout extra large above 300`() {
        val s = WidgetSizing.Size(350, 350)
        assertEquals(R.layout.widget_home_screen_extra_large, WidgetSizing.pickLayout(s))
    }

    @Test
    fun `compute font fits in width bound`() {
        val s = WidgetSizing.Size(100, 100)
        val f = WidgetSizing.compute(s, 52, showLabel = true)
        assertTrue("number sp positive", f.numberSp > 0)
        assertTrue("number sp not absurd", f.numberSp < 500f)
        assertTrue("label sp positive when shown", f.labelSp > 0)
    }

    @Test
    fun `compute label zero when hidden`() {
        val s = WidgetSizing.Size(180, 180)
        val f = WidgetSizing.compute(s, 7, showLabel = false)
        assertEquals(0f, f.labelSp, 0.001f)
    }

    @Test
    fun `tall widget stacks digits vertically`() {
        val s = WidgetSizing.Size(80, 200)
        val f = WidgetSizing.compute(s, 52, showLabel = false)
        assertTrue("digits stacked with newline", f.numberText.contains("\n"))
    }

    @Test
    fun `wide widget keeps digits horizontal`() {
        val s = WidgetSizing.Size(200, 100)
        val f = WidgetSizing.compute(s, 52, showLabel = false)
        assertEquals("52", f.numberText)
    }

    @Test
    fun `single digit never stacks`() {
        val s = WidgetSizing.Size(80, 200)
        val f = WidgetSizing.compute(s, 5, showLabel = false)
        assertEquals("5", f.numberText)
    }
}
