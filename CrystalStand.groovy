// code here

// make a GENTLY rounded cylinder

def radius = 3 + 3/8 // 3 + 3/8 inches
radius = radius * 25.4 // conv to mm

CSG roundedCylinder = new RoundedCylinder(radius,60.0)
                                .cornerRadius(10)// sets the radius of the corner
                                .toCSG()// converts it to a CSG tor display
CSG body = roundedCylinder

// make an identical non-rounded cylinder but half as tall
// union
// diff w a Wedge or Isosceles
// diff w an internal cylinder



return body
