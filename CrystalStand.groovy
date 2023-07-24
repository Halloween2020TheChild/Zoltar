// code here

// make a GENTLY rounded cylinder

def radius = 3 + 3/8 // 3 + 3/8 inches
radius = radius * 25.4 // conv to mm
def height = 2 + 7/8
height = height * 25.4

CSG top = new RoundedCylinder(radius,height)
                                .cornerRadius(10)// sets the radius of the corner
                                .toCSG()// converts it to a CSG tor display

CSG bottom = new RoundedCylinder(radius,height/2)
                                .cornerRadius(2)// sets the radius of the corner
                                .toCSG()// converts it to a CSG tor display
CSG body = top.union(bottom)

// make an identical non-rounded cylinder but half as tall
// union
// diff w a Wedge or Isosceles
// diff w an internal cylinder



return body
