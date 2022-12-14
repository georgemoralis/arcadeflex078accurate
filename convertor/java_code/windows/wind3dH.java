//============================================================
//
//	wind3d.h - Win32 Direct3D 7 (with DirectDraw 7) code
//
//============================================================

#ifndef __WIN32_D3D__
#define __WIN32_D3D__

/*
 * ported to v0.78
 * using automatic conversion tool v0.1.0
 */ 
package windows;

public class wind3dH
{
	
	
	//============================================================
	//	GLOBAL VARIABLES
	//============================================================
	
	
	
	
	
	//============================================================
	//	PROTOTYPES
	//============================================================
	
	int win_d3d_init(int width, int height, int depth, int attributes, double aspect, const struct win_effect_data *effect);
	void win_d3d_kill(void);
	int win_d3d_draw(struct mame_bitmap *bitmap, const struct rectangle *bounds, void *vector_dirty_pixels, int update);
	void win_d3d_wait_vsync(void);
	
	
	
	#endif
}
