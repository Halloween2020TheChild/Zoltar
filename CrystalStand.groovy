// get parameters & normalize

def radius = 3 + 3/8 // 3 + 3/8 inches
radius = radius/2
radius = radius * 25.4 // conv to mm
def height = 6 // 6mm
def radius_inner = 2 + 7/8
radius_inner = radius_inner/2
radius_inner = radius_inner * 25.4 // conv to mm
def cutout_degree = 120

// make a gently rounded cylinder
CSG top = new RoundedCylinder(radius,height)
                                .cornerRadius(1.5)// sets the radius of the corner
                                .toCSG()// converts it to a CSG tor display

//// make a subtly rounded cylinder
//CSG bottom = new RoundedCylinder(radius,height/2)
//                                .cornerRadius(1)// sets the radius of the corner
//                                .toCSG()// converts it to a CSG tor display

// union the two
//CSG body = top.union(bottom)
CSG body = top

// diff w a Wedge or Isosceles
// calculate width using trigonometry (sine)
def width = radius * Math.sin(Math.toRadians(cutout_degree/2))
width = width * 2
CSG cutout = new Isosceles(height,width,radius)
				.toCSG()
				.roty(90)
				.movex(radius)
body = body.difference(cutout)

// diff w an internal cylinder
CSG inner = new RoundedCylinder(radius_inner,height)
                                .cornerRadius(0)// sets the radius of the corner
                                .toCSG()// converts it to a CSG tor display
body = body.difference(inner)

return body