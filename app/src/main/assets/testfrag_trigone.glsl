#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
#else
precision mediump float;
#endif

uniform float u_time;
uniform vec2 u_resolution;
uniform vec2 u_offset;
//uniform sampler2D u_backbuffer;

#define M_PI 3.1415926535897932384626433832795

float hash( float n )
{
    return fract(sin(n)*43758.5453);
}

void main()
{
	float mx = max( u_resolution.x, u_resolution.y );
	vec2 uv = (gl_FragCoord.xy - u_resolution.xy*0.5)/mx;
    vec2 uv2 = gl_FragCoord.xy/mx;
	//uv += uv*0.3;
	float r = 0.5;

    //rotate

	uv *= mat2(
	r, -r,
	r, r );

	float y = mx*(uv.x)*0.05 + u_time*2. + hash(uv.x/100000000.);//+ u_time
	float f = 0.7;
	//f = (max( 0.4, min( f, 1.0 - f ) ) - 0.4)*10.0;

	vec3 color =
		vec3(
			mod( y + uv.x , 3. )*f,
			mod( y , 1.)*f/10.,
			mod( y*10. + uv.x*2., 0.9 )*f )*
			abs(fract(-uv.x*22.)  );


    vec3 color2 = vec3(0.07+0.62*uv.x,0.04+0.15*uv.x,0.18+0.09*uv.x);
    color = mix(color,color2,0.7);

	gl_FragColor = vec4( color, 1.0 );
}
