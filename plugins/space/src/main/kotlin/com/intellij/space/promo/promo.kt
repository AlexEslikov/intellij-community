// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.space.promo

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.panel.ComponentPanelBuilder
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import com.intellij.space.messages.SpaceBundle
import com.intellij.ui.components.BrowserLink
import com.intellij.ui.scale.ScaleContext
import com.intellij.ui.scale.ScaleType
import com.intellij.util.FontUtil
import com.intellij.util.JBHiDPIScaledImage
import com.intellij.util.SVGLoader
import com.intellij.util.ui.*
import icons.SpaceIcons
import libraries.klogging.logger
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Image
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.imageio.ImageIO
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JLayeredPane

internal const val SPACE_PROMO_VIDEO_URL: String = "https://www.youtube.com/watch?v=7-UNfbEjcNM"
internal const val DISCOVER_SPACE_PROMO_URL: String = "https://jetbrains.space"
internal const val SIGN_UP_SPACE_URL: String = "https://www.jetbrains.com/space/#sign-up"

internal const val SPACE_TOOLBAR_PROMO_BANNER_PATH = "/images/spacePromo.png"
internal const val SPACE_TOOLBAR_PROMO_BANNER_PATH_RETINA = "/images/spacePromo@2x.png"

internal const val SPACE_BIG_PROMO_BANNER_PATH = "/images/spaceBigPromoBanner.png"
internal const val SPACE_BIG_PROMO_BANNER_PATH_RETINA = "/images/spaceBigPromoBanner@2x.png"

internal const val JETBRAINS_SPACE_LOGO = "/images/jetbrainsSpace.svg"
internal const val JETBRAINS_SPACE_LOGO_DARK = "/images/jetbrainsSpaceDark.svg"

internal fun toolbarPromoBanner(): JComponent? {
  val imagePath = if (StartupUiUtil.isJreHiDPI()) SPACE_TOOLBAR_PROMO_BANNER_PATH_RETINA else SPACE_TOOLBAR_PROMO_BANNER_PATH
  val image = ImageLoader.loadImage(imagePath, 364, 199) ?: return null

  return wrapWithWatchSpaceOverviewLabelOverlay(JLabel(JBImageIcon(image)))
}

internal fun bigPromoBanner(): JComponent? = bigPromoBanner(400, 260)

internal fun bigPromoBanner(width: Int, height: Int): JComponent? {
  val imagePath = if (StartupUiUtil.isJreHiDPI()) SPACE_BIG_PROMO_BANNER_PATH_RETINA else SPACE_BIG_PROMO_BANNER_PATH
  val image = ImageLoader.loadImage(imagePath, width, height) ?: return null

  return wrapWithWatchSpaceOverviewLabelOverlay(JLabel(JBImageIcon(image)), alwaysDisplayLabel = true)
}

internal fun spaceLinkLabel(): BrowserLink = BrowserLink(SpaceBundle.message("product.name"), DISCOVER_SPACE_PROMO_URL).apply {
  isFocusPainted = false
}

internal fun wrapWithWatchSpaceOverviewLabelOverlay(component: JComponent, alwaysDisplayLabel: Boolean = false): JComponent {
  @NlsSafe val text = "\u25B6${FontUtil.spaceAndThinSpace()}${SpaceBundle.message("space.promo.watch.space.overview.label")}"

  val watchVideoButton = JLabel(text).apply {
    val labelColor = Color(0, 0, 0, 180)
    isFocusable = false
    isOpaque = true
    isVisible = alwaysDisplayLabel
    background = labelColor
    foreground = Color.WHITE
    border = JBUI.Borders.empty(8)
  }

  return JComponentOverlay.createCentered(component, watchVideoButton).apply {
    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

    addMouseListener(object : MouseAdapter() {
      override fun mouseEntered(e: MouseEvent?) {
        if (!alwaysDisplayLabel) watchVideoButton.isVisible = true
      }

      override fun mouseExited(e: MouseEvent?) {
        if (!alwaysDisplayLabel) watchVideoButton.isVisible = false
      }

      override fun mouseClicked(e: MouseEvent?) = watchPromoVideo()
    })
  }
}

internal fun createSpaceByJetbrainsLabel(): JComponent = JLabel().apply {
  val jbSpaceSvgLogo = SvgLoader.loadSvg(if (StartupUiUtil.isUnderDarcula()) JETBRAINS_SPACE_LOGO_DARK else JETBRAINS_SPACE_LOGO, this)
  if (jbSpaceSvgLogo != null) {
    icon = jbSpaceSvgLogo
  } else {
    icon = SpaceIcons.Main
    text = SpaceBundle.message("product.name.jetbrains.space")
  }
}

internal fun fullPromoText(maxLineLength: Int = 0): JLabel = promoText(SpaceBundle.message("space.promo.text.full"), maxLineLength)

internal fun sharePromoText(maxLineLength: Int = 0): JLabel = promoText(SpaceBundle.message("space.promo.text.share"), maxLineLength)

private fun promoText(@NlsContexts.DetailedDescription text: String, maxLineLength: Int): JLabel =
  ComponentPanelBuilder.createCommentComponent(text, true, maxLineLength).apply {
    foreground = JBUI.CurrentTheme.Label.foreground()
    font = JBUI.Fonts.label()
  }

private fun watchPromoVideo() {
  BrowserUtil.browse(SPACE_PROMO_VIDEO_URL)
}

private object JComponentOverlay {
  fun createCentered(component: JComponent, centeredOverlay: JComponent): JLayeredPane {
    val pane = object : JLayeredPane() {
      override fun getPreferredSize(): Dimension = component.preferredSize

      override fun doLayout() {
        super.doLayout()
        component.setBounds(0, 0, width, height)
        centeredOverlay.bounds = SingleComponentCenteringLayout.getBoundsForCentered(component, centeredOverlay)
      }
    }
    pane.isFocusable = false
    pane.add(component, JLayeredPane.DEFAULT_LAYER, 1)
    pane.add(centeredOverlay, JLayeredPane.DEFAULT_LAYER, 0)
    return pane
  }
}

val log = logger<ImageLoader>()

private object ImageLoader {

  fun loadImage(path: String, width: Int, height: Int): Image? {
    return try {
      val img = ImageIO.read(javaClass.getResourceAsStream(path))
      JBHiDPIScaledImage(img, width, height, img.type)
    }
    catch (e: Exception) {
      log.error { "Image ${path} is not loaded" }
      null
    }
  }
}

private object SvgLoader {
  fun loadSvg(path: String, comp: JComponent): JBImageIcon? = try {
    val bytes = javaClass.getResourceAsStream(path)?.readAllBytes()
    val ctx = ScaleContext.create(comp)
    val image = SVGLoader.loadWithoutCache(bytes, ctx.getScale(ScaleType.SYS_SCALE).toFloat())
    val hiDpi = ImageUtil.ensureHiDPI(image, ctx)
    JBImageIcon(hiDpi)
  }
  catch (e: Exception) {
    log.error { "Svg ${path} is not loaded" }
    null
  }
}
