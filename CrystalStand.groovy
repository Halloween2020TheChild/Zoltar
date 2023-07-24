// get parameters & normalize

def radius = 3 + 3/8 // 3 + 3/8 inches
radius = radius * 25.4 // conv to mm
def height = 60 // 6mm
def radius_inner = 3 + 3/8 // 3 + 3/8 inches
radius_inner = radius_inner * 25.4 // conv to mm
def cutout_degree = 120

// make a gently rounded cylinder
CSG top = new RoundedCylinder(radius,height)
                                .cornerRadius(10)// sets the radius of the corner
                                .toCSG()// converts it to a CSG tor display

// make a subtly rounded cylinder
CSG bottom = new RoundedCylinder(radius,height/2)
                                .cornerRadius(2)// sets the radius of the corner
                                .toCSG()// converts it to a CSG tor display

// union the two
CSG body = top.union(bottom)

// diff w a Wedge or Isosceles
//CSG cutout = new Isosceles(60,25,40).toCSG()

// diff w an internal cylinder



return body
