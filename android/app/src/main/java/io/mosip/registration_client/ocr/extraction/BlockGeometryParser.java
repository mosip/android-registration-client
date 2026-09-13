/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.extraction;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class BlockGeometryParser {

    private BlockGeometryParser() {}

    @NonNull
    public static List<LayoutRow> parse(@NonNull Text visionText) {
        List<LineFragment> fragments = collectFragments(visionText);
        if (fragments.isEmpty()) return Collections.emptyList();

        Collections.sort(fragments, Comparator.comparingInt(f -> f.box.top));

        List<List<LineFragment>> rowGroups = groupIntoRows(fragments);

        return buildRows(rowGroups);
    }

    @NonNull
    public static List<LayoutRow> parseFlat(@NonNull String[] lines) {
        List<LayoutRow> rows = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            // Bounding box: use row index as y so rows stay ordered
            Rect placeholderBox = new Rect(0, rows.size(), 1, rows.size() + 1);
            LineFragment frag = new LineFragment(trimmed, placeholderBox);
            List<LineFragment> frags = new ArrayList<>();
            frags.add(frag);
            rows.add(new LayoutRow(trimmed, frags, new Rect(placeholderBox)));
        }
        return rows;
    }

    public static class LineFragment {
        @NonNull public final String text;
        @NonNull public final Rect   box;

        public LineFragment(@NonNull String text, @NonNull Rect box) {
            this.text = text;
            this.box  = box;
        }
    }

    public static class LayoutRow {
        @NonNull public final String fullText;
        @NonNull public final List<LineFragment> fragments;
        @NonNull public final Rect bounds;

        LayoutRow(
                @NonNull String fullText,
                @NonNull List<LineFragment> fragments,
                @NonNull Rect bounds) {
            this.fullText  = fullText;
            this.fragments = Collections.unmodifiableList(new ArrayList<>(fragments));
            this.bounds    = bounds;
        }
    }

    @NonNull
    private static List<LineFragment> collectFragments(@NonNull Text visionText) {
        List<LineFragment> result = new ArrayList<>();
        for (Text.TextBlock block : visionText.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                Rect   box = line.getBoundingBox();
                String txt = line.getText();
                if (box == null || txt == null || txt.trim().isEmpty()) continue;
                result.add(new LineFragment(txt.trim(), new Rect(box)));
            }
        }
        return result;
    }

    @NonNull
    private static List<List<LineFragment>> groupIntoRows(
            @NonNull List<LineFragment> sortedFragments) {

        List<List<LineFragment>> groups = new ArrayList<>();

        for (LineFragment frag : sortedFragments) {
            List<LineFragment> bestGroup = null;
            int bestOverlap = 0;
            int fragCY = centerY(frag.box);
            int fragH = Math.max(frag.box.bottom - frag.box.top, 1);

            for (List<LineFragment> group : groups) {
                // Do not merge if frag horizontally overlaps significantly with an existing fragment
                // in the same row group (meaning they are stacked in the same column).
                if (hasColumnCollision(group, frag)) {
                    continue;
                }

                Rect rowBounds = getRowBounds(group);
                int rowH = averageHeight(group);
                int rowCY = centerY(rowBounds);

                int overlapY = Math.max(0, Math.min(rowBounds.bottom, frag.box.bottom)
                        - Math.max(rowBounds.top, frag.box.top));
                int minH = Math.min(rowH, fragH);

                // Significant vertical overlap OR vertical centers very close
                boolean isMatch = (overlapY >= 0.35f * minH)
                        || (Math.abs(fragCY - rowCY) <= Math.max((int) (minH * 0.55f), 10));

                if (isMatch) {
                    if (overlapY > bestOverlap || bestGroup == null) {
                        bestOverlap = overlapY;
                        bestGroup = group;
                    }
                }
            }

            if (bestGroup != null) {
                bestGroup.add(frag);
            } else {
                List<LineFragment> newGroup = new ArrayList<>();
                newGroup.add(frag);
                groups.add(newGroup);
            }
        }

        // Sort rows vertically by top coordinate
        groups.sort(Comparator.comparingInt(g -> getRowBounds(g).top));
        return groups;
    }

    private static boolean hasColumnCollision(
            @NonNull List<LineFragment> group,
            @NonNull LineFragment frag) {
        int fragW = Math.max(frag.box.right - frag.box.left, 1);
        for (LineFragment other : group) {
            int overlapX = Math.max(0, Math.min(frag.box.right, other.box.right)
                    - Math.max(frag.box.left, other.box.left));
            int otherW = Math.max(other.box.right - other.box.left, 1);
            int minW = Math.min(fragW, otherW);
            if (overlapX >= 0.50f * minW) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    private static Rect getRowBounds(@NonNull List<LineFragment> group) {
        if (group.isEmpty()) return new Rect();
        Rect r = new Rect(group.get(0).box);
        for (int i = 1; i < group.size(); i++) {
            r = union(r, group.get(i).box);
        }
        return r;
    }

    @NonNull
    private static List<LayoutRow> buildRows(@NonNull List<List<LineFragment>> rowGroups) {
        List<LayoutRow> rows = new ArrayList<>();
        for (List<LineFragment> group : rowGroups) {
            // Sort L→R within the row
            Collections.sort(group, Comparator.comparingInt(f -> f.box.left));

            StringBuilder sb       = new StringBuilder();
            Rect          unionBox = null;

            for (LineFragment frag : group) {
                if (sb.length() > 0) sb.append("  "); // preserve visual gap
                sb.append(frag.text);
                unionBox = (unionBox == null) ? new Rect(frag.box) : union(unionBox, frag.box);
            }

            String text = sb.toString().trim();
            if (!text.isEmpty() && unionBox != null) {
                rows.add(new LayoutRow(text, group, unionBox));
            }
        }
        return rows;
    }

    private static int centerY(@NonNull Rect r) {
        return (r.top + r.bottom) / 2;
    }

    private static int averageHeight(@NonNull List<LineFragment> group) {
        int total = 0;
        for (LineFragment f : group) total += (f.box.bottom - f.box.top);
        return Math.max(total / group.size(), 1);
    }

    @NonNull
    private static Rect union(@NonNull Rect a, @NonNull Rect b) {
        return new Rect(
                Math.min(a.left,   b.left),
                Math.min(a.top,    b.top),
                Math.max(a.right,  b.right),
                Math.max(a.bottom, b.bottom)
        );
    }
}
