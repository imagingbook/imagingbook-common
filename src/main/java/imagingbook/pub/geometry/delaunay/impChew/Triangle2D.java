package imagingbook.pub.geometry.delaunay.impChew;

/*
 * Copyright (c) 2007 by L. Paul Chew.
 *
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import imagingbook.pub.geometry.delaunay.common.Point;
import imagingbook.pub.geometry.delaunay.common.Triangle;


/**
 * A Triangle is an immutable Set of exactly three Pnts.
 *
 * All Set operations are available. Individual vertices can be accessed via
 * iterator() and also via triangle.get(index).
 *
 * Note that, even if two triangles have the same vertex set, they are
 * *different* triangles. Methods equals() and hashCode() are consistent with
 * this rule.
 *
 * @author Paul Chew
 *
 * Created December 2007. Replaced general simplices with geometric triangle.
 *
 */
public class Triangle2D extends ArraySet<Vector2D> implements Triangle {

    private int idNumber;                   // The id number
    private Vector2D circumcenter = null;        // The triangle's circumcenter

    private static int idGenerator = 0;     // Used to create id numbers
    public static boolean moreInfo = true; // True iff more info in toString

    /**
     * @param vertices the vertices of the Triangle.
     * @throws IllegalArgumentException if there are not three distinct vertices
     */
    public Triangle2D (Vector2D... vertices) {
        this(Arrays.asList(vertices));
    }

    /**
     * @param collection a Collection holding the Simplex vertices
     * @throws IllegalArgumentException if there are not three distinct vertices
     */
    public Triangle2D (Collection<? extends Vector2D> collection) {
        super(collection);
        idNumber = idGenerator++;
        if (this.size() != 3)
            throw new IllegalArgumentException("Triangle must have 3 vertices");
    }
    
	public Triangle2D(Point[] points) {
		this(new Vector2D(points[0]), new Vector2D(points[1]), new Vector2D(points[2]));
	}
    
    // ----------------------------------------------------------------------------

    @Override
    public String toString () {
        if (!moreInfo) return "Triangle" + idNumber;
        return "Triangle" + idNumber + super.toString();
    }

    /**
     * Get arbitrary vertex of this triangle, but not any of the bad vertices.
     * @param badVertices one or more bad vertices
     * @return a vertex of this triangle, but not one of the bad vertices
     * @throws NoSuchElementException if no vertex found
     */
    public Vector2D getVertexButNot (Vector2D... badVertices) {
        Collection<Vector2D> bad = Arrays.asList(badVertices);
        for (Vector2D v: this) if (!bad.contains(v)) return v;
        throw new NoSuchElementException("No vertex found");
    }

    /**
     * True iff triangles are neighbors. Two triangles are neighbors if they
     * share a facet.
     * @param triangle the other Triangle
     * @return true iff this Triangle is a neighbor of triangle
     */
    public boolean isNeighbor (Triangle2D triangle) {
        int count = 0;
        for (Vector2D vertex: this)
            if (!triangle.contains(vertex)) count++;
        return count == 1;
    }

    /**
     * Report the facet opposite vertex.
     * @param vertex a vertex of this Triangle
     * @return the facet opposite vertex
     * @throws IllegalArgumentException if the vertex is not in triangle
     */
    public ArraySet<Vector2D> facetOpposite (Vector2D vertex) {
        ArraySet<Vector2D> facet = new ArraySet<Vector2D>(this);
        if (!facet.remove(vertex))
            throw new IllegalArgumentException("Vertex not in triangle");
        return facet;
    }

    /**
     * @return the triangle's circumcenter
     */
    public Vector2D getCircumcenter () {
        if (circumcenter == null)
            circumcenter = Vector2D.circumcenter(this.toArray(new Vector2D[0]));
        return circumcenter;
    }

    /* The following two methods ensure that a Triangle is immutable */

    @Override
    public boolean add (Vector2D vertex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Vector2D> iterator () {
        return new Iterator<Vector2D>() {
            private Iterator<Vector2D> it = Triangle2D.super.iterator();
            public boolean hasNext() {return it.hasNext();}
            public Vector2D next() {return it.next();}
            public void remove() {throw new UnsupportedOperationException();}
        };
    }

    /* The following two methods ensure that all triangles are different. */

    @Override
    public int hashCode () {
        return (int)(idNumber^(idNumber>>>32));
    }

    @Override
    public boolean equals (Object o) {
        return (this == o);
    }

    // method required by {@link Triangle} interface:
    
	@Override
	public Point[] getPoints() {
		return this.toArray(new Point[0]);
	}

}