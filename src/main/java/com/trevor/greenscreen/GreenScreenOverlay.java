package com.trevor.greenscreen;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GreenScreenOverlay extends Overlay
{
	private Client client;
	private GreenScreenConfig config;
	private GreenScreenPlugin plugin;

	@Inject
	public GreenScreenOverlay(Client client, GreenScreenPlugin plugin, GreenScreenConfig config) {
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.config = config;
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics) {

		if (!plugin.isRenderGreenscreen())
		{
			return null;
		}

		BufferedImage image = new BufferedImage(client.getCanvasWidth(), client.getCanvasHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = image.getGraphics();

		g.setColor(config.greenscreenColor());
		g.fillRect(0, 0, image.getWidth(), image.getHeight());

		Polygon[] polygons = getPolygons();
		Triangle[] triangles = getTriangles(client.getLocalPlayer().getModel());

		for (int i = 0; i < polygons.length; i++) {
			Triangle t = triangles[i];
			if (!(t.getA().getY() == 6 && t.getB().getY() == 6 && t.getC().getY() == 6)) {
				clearPolygon(image, polygons[i]);
			}
		}

		graphics.drawImage(image, 0, 0, null);

		return null;
	}

	private void clearPolygon(BufferedImage image, Polygon p) {
		Rectangle bounds = p.getBounds();
		for (double y = bounds.getMinY(); y < bounds.getMaxY(); y++) {
			for (double x = bounds.getMinX(); x < bounds.getMaxX(); x++) {
				if (p.contains(x, y)
					&& x >= 0
					&& x < client.getCanvasWidth()
					&& y >= 0
					&& y < client.getCanvasHeight()
				) {
					image.setRGB((int)x, (int)y, 0x00000000);
				}
			}
		}
	}

	private List<Vertex> getVertices(Model model)
	{
		float[] verticesX = model.getVerticesX();
		float[] verticesY = model.getVerticesY();
		float[] verticesZ = model.getVerticesZ();

		int count = model.getVerticesCount();

		List<Vertex> vertices = new ArrayList(count);

		for (int i = 0; i < count; ++i)
		{
			Vertex v = new Vertex(
				verticesX[i],
				verticesY[i],
				verticesZ[i]
			);
			vertices.add(v);
		}

		return vertices;
	}

	private Triangle[] getTriangles(Model model)
	{
		int[] trianglesX = model.getFaceIndices1();
		int[] trianglesY = model.getFaceIndices2();
		int[] trianglesZ = model.getFaceIndices3();

		List<Vertex> vertices = getVertices(model);

		int count = model.getFaceCount();
		Triangle[] triangles = new Triangle[count];

		for (int i = 0; i < count; ++i)
		{
			int triangleX = trianglesX[i];
			int triangleY = trianglesY[i];
			int triangleZ = trianglesZ[i];

			Triangle triangle = new Triangle(
				vertices.get(triangleX),
				vertices.get(triangleY),
				vertices.get(triangleZ)
			);
			triangles[i] = triangle;
		}

		return triangles;
	}

	private Polygon[] getPolygons()
	{
		Player local = client.getLocalPlayer();
		Model m = local.getModel();

		if (m == null)
		{
			return null;
		}

		int[] x2d = new int[m.getVerticesCount()];
		int[] y2d = new int[m.getVerticesCount()];

		WorldView wv = client.getTopLevelWorldView();
		LocalPoint point = local.getLocalLocation();
		int x = point.getX();
		int y = point.getY();

		int height = Perspective.getFootprintTileHeight(client, point, wv.getPlane(), local.getFootprintSize());
		height -= local.getAnimationHeightOffset();

		Perspective.modelToCanvas(client, wv,
				m.getVerticesCount(),
				x, y, height,
				local.getCurrentOrientation(),
				m.getVerticesX(), m.getVerticesZ(), m.getVerticesY(),
				x2d, y2d);

		List<Polygon> polys = new ArrayList<>(m.getFaceCount());

		int[] indices1 = m.getFaceIndices1();
		int[] indices2 = m.getFaceIndices2();
		int[] indices3 = m.getFaceIndices3();

		int[] xs = new int[3];
		int[] ys = new int[3];
		for (int tri = 0; tri < m.getFaceCount(); tri++)
		{
			int idx;

			idx = indices1[tri];
			xs[0] = x2d[idx];
			ys[0] = y2d[idx];

			idx = indices2[tri];
			xs[1] = x2d[idx];
			ys[1] = y2d[idx];

			idx = indices3[tri];
			xs[2] = x2d[idx];
			ys[2] = y2d[idx];

			polys.add(new Polygon(xs, ys, 3));
		}

		return polys.toArray(new Polygon[0]);
	}

}
