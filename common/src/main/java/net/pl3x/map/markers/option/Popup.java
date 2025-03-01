package net.pl3x.map.markers.option;

import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.JsonObjectWrapper;
import net.pl3x.map.markers.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Popup properties of a marker.
 */
public class Popup extends Option<Popup> {
    private String content;
    private String pane;
    private Point offset;
    private Integer maxWidth;
    private Integer minWidth;
    private Integer maxHeight;
    private Boolean autoPan;
    private Point autoPanPaddingTopLeft;
    private Point autoPanPaddingBottomRight;
    private Point autoPanPadding;
    private Boolean keepInView;
    private Boolean closeButton;
    private Boolean autoClose;
    private Boolean closeOnEscapeKey;
    private Boolean closeOnClick;

    /**
     * Create a popup rule with default options.
     */
    public Popup() {
    }

    /**
     * Create a popup rule.
     *
     * @param content popup content
     */
    public Popup(@Nullable String content) {
        setContent(content);
    }

    /**
     * Get the content of this popup rule.
     * <p>
     * If null, the popup rule is effectively disabled.
     *
     * @return popup content
     */
    @Nullable
    public String getContent() {
        return this.content;
    }

    /**
     * Set the content for this popup rule.
     * <p>
     * HTML is valid here.
     * <p>
     * If null, the popup rule is effectively disabled.
     *
     * @param content popup content
     * @return this popup rule
     */
    @NotNull
    public Popup setContent(@Nullable String content) {
        this.content = content;
        return this;
    }

    /**
     * Get the map pane where the popup will be added.
     * <p>
     * Defaults to '<code>popupPane</code>' if null.
     *
     * @return map pane
     */
    @Nullable
    public String getPane() {
        return this.pane;
    }

    /**
     * Set the map pane where the popup will be added.
     * <p>
     * If the pane does not exist, it will be created the first time it is used.
     * <p>
     * Defaults to '<code>popupPane</code>' if null.
     *
     * @param pane map pane
     * @return this popup rule
     */
    @NotNull
    public Popup setPane(@Nullable String pane) {
        this.pane = pane;
        return this;
    }

    /**
     * Get offset of this popup rule.
     * <p>
     * Defaults to '<code>new {@link Point}(0, 7)</code>' if null.
     *
     * @return popup offset from marker point
     */
    @Nullable
    public Point getOffset() {
        return this.offset;
    }

    /**
     * Set offset of this popup rule from marker point
     * <p>
     * Defaults to '<code>new {@link Point}(0, 7)</code>' if null.
     *
     * @param offset popup offset
     * @return this popup rule
     */
    @NotNull
    public Popup setOffset(@Nullable Point offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Get the max width of the popup.
     * <p>
     * Defaults to '<code>300</code>' if null.
     *
     * @return max width
     */
    @Nullable
    public Integer getMaxWidth() {
        return this.maxWidth;
    }

    /**
     * Set the max width of the popup.
     * <p>
     * Defaults to '<code>300</code>' if null.
     *
     * @param maxWidth max width
     * @return this popup rule
     */
    @NotNull
    public Popup setMaxWidth(@Nullable Integer maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    /**
     * Get the min width of the popup.
     * <p>
     * Defaults to '<code>50</code>' if null.
     *
     * @return min width
     */
    @Nullable
    public Integer getMinWidth() {
        return this.minWidth;
    }

    /**
     * Set the min width of the popup.
     * <p>
     * Defaults to '<code>50</code>' if null.
     *
     * @param minWidth min width
     * @return this popup rule
     */
    @NotNull
    public Popup setMinWidth(@Nullable Integer minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    /**
     * Get the max height of the popup.
     * <p>
     * If set, creates a scrollable container of the given
     * height inside a popup if its content exceeds it.
     *
     * @return max height
     */
    @Nullable
    public Integer getMaxHeight() {
        return this.maxHeight;
    }

    /**
     * Set the max height of the popup.
     * <p>
     * If set, creates a scrollable container of the given
     * height inside a popup if its content exceeds it.
     *
     * @param maxHeight max height
     * @return this popup rule
     */
    @NotNull
    public Popup setMaxHeight(@Nullable Integer maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    /**
     * Get whether the map should automatically pan to fit the opened popup.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @return true to auto pan
     */
    @Nullable
    public Boolean shouldAutoPan() {
        return this.autoPan;
    }

    /**
     * Set whether the map should automatically pan to fit the opened popup.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @param autoPan true to auto pan
     * @return this popup rule
     */
    @NotNull
    public Popup setShouldAutoPan(@Nullable Boolean autoPan) {
        this.autoPan = autoPan;
        return this;
    }

    /**
     * Get the margin between the popup and the top left corner of the map view
     * after auto panning was performed.
     * <p>
     * If set, overrides the top left values of {@link #getAutoPanPadding()}.
     *
     * @return top left corner padding margins
     */
    @Nullable
    public Point getAutoPanPaddingTopLeft() {
        return this.autoPanPaddingTopLeft;
    }

    /**
     * Set the margin between the popup and the top left corner of the map view
     * after auto panning was performed.
     * <p>
     * If set, overrides the top left values of {@link #getAutoPanPadding()}.
     *
     * @param autoPanPaddingTopLeft top left corner padding margins
     * @return this popup rule
     */
    @NotNull
    public Popup setAutoPanPaddingTopLeft(@Nullable Point autoPanPaddingTopLeft) {
        this.autoPanPaddingTopLeft = autoPanPaddingTopLeft;
        return this;
    }

    /**
     * Get the margin between the popup and the bottom right corner of the map view
     * after auto panning was performed.
     * <p>
     * If set, overrides the bottom right values of {@link #getAutoPanPadding()}.
     *
     * @return bottom right corner padding margins
     */
    @Nullable
    public Point getAutoPanPaddingBottomRight() {
        return this.autoPanPaddingBottomRight;
    }

    /**
     * Set the margin between the popup and the bottom right corner of the map view
     * after auto panning was performed.
     * <p>
     * If set, overrides the bottom right values of {@link #getAutoPanPadding()}.
     *
     * @param autoPanPaddingBottomRight bottom right corner padding margins
     * @return this popup rule
     */
    @NotNull
    public Popup setAutoPanPaddingBottomRight(@Nullable Point autoPanPaddingBottomRight) {
        this.autoPanPaddingBottomRight = autoPanPaddingBottomRight;
        return this;
    }

    /**
     * Get the margin between the popup and the map view after auto panning was performed.
     * <p>
     * This is the equivalent of the same values in both {@link #getAutoPanPaddingTopLeft()}
     * and {@link #getAutoPanPaddingBottomRight()}
     * <p>
     * Defaults to '<code>new {@link Point}(5, 5)</code>' if null.
     *
     * @return padding margins
     */
    @Nullable
    public Point getAutoPanPadding() {
        return this.autoPanPadding;
    }

    /**
     * Set the margin between the popup and the map view after auto panning was performed.
     * <p>
     * This is the equivalent of the same values in both {@link #getAutoPanPaddingTopLeft()}
     * and {@link #getAutoPanPaddingBottomRight()}
     * <p>
     * Defaults to '<code>new {@link Point}(5, 5)</code>' if null.
     *
     * @param autoPanPadding padding margins
     * @return this popup rule
     */
    @NotNull
    public Popup setAutoPanPadding(@Nullable Point autoPanPadding) {
        this.autoPanPadding = autoPanPadding;
        return this;
    }

    /**
     * Get whether the popup should stay in view.
     * <p>
     * If set to true it will prevent users from panning the popup off of the screen while it is open.
     * <p>
     * Defaults to '<code>false</code>' if null.
     *
     * @return true to keep popup in view
     */
    @Nullable
    public Boolean shouldKeepInView() {
        return this.keepInView;
    }

    /**
     * Set whether the popup should stay in view.
     * <p>
     * If set to true it will prevent users from panning the popup off of the screen while it is open.
     * <p>
     * Defaults to '<code>false</code>' if null.
     *
     * @param keepInView true to keep popup in view
     * @return this popup rule
     */
    @NotNull
    public Popup setShouldKeepInView(@Nullable Boolean keepInView) {
        this.keepInView = keepInView;
        return this;
    }

    /**
     * Get whether the popup has a close button.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @return true if popup has close button
     */
    @Nullable
    public Boolean hasCloseButton() {
        return this.closeButton;
    }

    /**
     * Set whether the popup has a close button.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @param closeButton true if popup has close button
     * @return this popup rule
     */
    @NotNull
    public Popup setCloseButton(@Nullable Boolean closeButton) {
        this.closeButton = closeButton;
        return this;
    }

    /**
     * Get whether the popup automatically closes when another popup is opened.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @return true if popup auto closes
     */
    @Nullable
    public Boolean shouldAutoClose() {
        return this.autoClose;
    }

    /**
     * Set whether the popup automatically closes when another popup is opened.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @param autoClose true if popup auto closes
     * @return this popup rule
     */
    @NotNull
    public Popup setShouldAutoClose(@Nullable Boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }

    /**
     * Get whether the popup closes with the escape key.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @return true to close with escape
     */
    @Nullable
    public Boolean shouldCloseOnEscapeKey() {
        return this.closeOnEscapeKey;
    }

    /**
     * Set whether the popup closes with the escape key.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @param closeOnEscapeKey true to close with escape
     * @return this popup rule
     */
    @NotNull
    public Popup setShouldCloseOnEscapeKey(@Nullable Boolean closeOnEscapeKey) {
        this.closeOnEscapeKey = closeOnEscapeKey;
        return this;
    }

    /**
     * Get whether the popup closes when the map is clicked.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @return true to close on map click
     */
    @Nullable
    public Boolean shouldCloseOnClick() {
        return this.closeOnClick;
    }

    /**
     * Set whether the popup closes when the map is clicked.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @param closeOnClick true to close on map click
     * @return this popup rule
     */
    @NotNull
    public Popup setShouldCloseOnClick(@Nullable Boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
        return this;
    }

    @Override
    public boolean isDefault() {
        return getContent() == null &&
                getPane() == null &&
                getOffset() == null &&
                getMaxWidth() == null &&
                getMinWidth() == null &&
                getMaxHeight() == null &&
                shouldAutoPan() == null &&
                getAutoPanPaddingTopLeft() == null &&
                getAutoPanPaddingBottomRight() == null &&
                getAutoPanPadding() == null &&
                shouldKeepInView() == null &&
                hasCloseButton() == null &&
                shouldAutoClose() == null &&
                shouldCloseOnEscapeKey() == null &&
                shouldCloseOnClick() == null;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("content", getContent());
        wrapper.addProperty("pane", getPane());
        wrapper.addProperty("offset", getOffset());
        wrapper.addProperty("maxWidth", getMaxWidth());
        wrapper.addProperty("minWidth", getMinWidth());
        wrapper.addProperty("maxHeight", getMaxHeight());
        wrapper.addProperty("autoPan", shouldAutoPan());
        wrapper.addProperty("autoPanPaddingTopLeft", getAutoPanPaddingTopLeft());
        wrapper.addProperty("autoPanPaddingBottomRight", getAutoPanPaddingBottomRight());
        wrapper.addProperty("autoPanPadding", getAutoPanPadding());
        wrapper.addProperty("keepInView", shouldKeepInView());
        wrapper.addProperty("closeButton", hasCloseButton());
        wrapper.addProperty("autoClose", shouldAutoClose());
        wrapper.addProperty("closeOnEscapeKey", shouldCloseOnEscapeKey());
        wrapper.addProperty("closeOnClick", shouldCloseOnClick());
        return wrapper.getJsonObject();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Popup other = (Popup) o;
        return Objects.equals(getContent(), other.getContent())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOffset(), other.getOffset())
                && isSizeEqual(other)
                && isPanningEqual(other)
                && isClosingEqual(other);
    }

    private boolean isSizeEqual(Popup other) {
        return Objects.equals(getMaxWidth(), other.getMaxWidth())
                && Objects.equals(getMinWidth(), other.getMinWidth())
                && Objects.equals(getMaxHeight(), other.getMaxHeight());
    }

    private boolean isPanningEqual(Popup other) {
        return Objects.equals(shouldAutoPan(), other.shouldAutoPan())
                && Objects.equals(getAutoPanPaddingTopLeft(), other.getAutoPanPaddingTopLeft())
                && Objects.equals(getAutoPanPaddingBottomRight(), other.getAutoPanPaddingBottomRight())
                && Objects.equals(getAutoPanPadding(), other.getAutoPanPadding())
                && Objects.equals(shouldKeepInView(), other.shouldKeepInView());
    }

    private boolean isClosingEqual(Popup other) {
        return Objects.equals(hasCloseButton(), other.hasCloseButton())
                && Objects.equals(shouldAutoClose(), other.shouldAutoClose())
                && Objects.equals(shouldCloseOnEscapeKey(), other.shouldCloseOnEscapeKey())
                && Objects.equals(shouldCloseOnClick(), other.shouldCloseOnClick());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent(), getPane(), getOffset(), getMaxWidth(), getMinWidth(), getMaxHeight(),
                shouldAutoPan(), getAutoPanPaddingTopLeft(), getAutoPanPaddingBottomRight(), getAutoPanPadding(),
                shouldKeepInView(), hasCloseButton(), shouldAutoClose(), shouldCloseOnEscapeKey(), shouldCloseOnClick());
    }

    @Override
    public String toString() {
        return "Popup{"
                + ",content=" + getContent()
                + ",pane=" + getPane()
                + ",offset=" + getOffset()
                + ",maxWidth=" + getMaxWidth()
                + ",minWidth=" + getMinWidth()
                + ",maxHeight=" + getMaxHeight()
                + ",autoPan=" + shouldAutoPan()
                + ",autoPanPaddingTopLeft=" + getAutoPanPaddingTopLeft()
                + ",autoPanPaddingBottomRight=" + getAutoPanPaddingBottomRight()
                + ",autoPanPadding=" + getAutoPanPadding()
                + ",keepInView=" + shouldKeepInView()
                + ",closeButton=" + hasCloseButton()
                + ",autoClose=" + shouldAutoClose()
                + ",closeOnEscapeKey=" + shouldCloseOnEscapeKey()
                + ",closeOnClick=" + shouldCloseOnClick()
                + "}";
    }
}
