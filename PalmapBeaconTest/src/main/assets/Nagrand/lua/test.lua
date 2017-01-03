-- 获取默认文字路径
local function GET_FONT_PATH()
  local engine = GetEngine()
  local properties = engine.properties
  local os = properties["os"]
  if os then
    if os >= "iOS/7.0" and os < "iOS/8.0" then
      return "/System/Library/Fonts/Cache/STHeiti-Light.ttc"
    elseif os >= "iOS/8.0" and os < "iOS/9.0" then
      return "/System/Library/Fonts/Core/STHeiti-Light.ttc"
    elseif os >= "iOS/9.0" then
      return "/System/Library/Fonts/LanguageSupport/PingFang.ttc"
    else
      return "/System/Library/Fonts/LanguageSupport/PingFang.ttc"
    end
  else
    return properties["lua_path"] .. "/DroidSansFallback.ttf"
  end
end

local function GET_CACHE_PATH()
  local engine = GetEngine()
  local properties = engine.properties

  return properties["cache_folder"]
end

local function DEFAULT_STYLE()
  return {
    ['2d'] = {
      style = 'polygon',
      face = {
        color = '0xff00ff00',
        enable_alpha = true,
        texture = nil, -- 纹理路径
        automatic_scale = nil, -- 是否自动缩放
        texture_rotation = 0, -- texture旋转角度[-360,360],顺时针为正
        edge_aligment = true -- 如果为矩形,texture旋转时自动与边对齐
      },
      outline = {
        color = '0xff000000',
		    alignment = 'AlignRight',
        width = 0.1,
        enable_width = true,--具备线宽的绘制方式
      },
      -- multi_polygon = {} -- multipolygon设置
    }
  }
end

-- 空类型样式，设置此类型的POI不渲染
local function NULLSTYLE()
  return {
    ['2d'] = {
      style = 'nullstyle',
    }
  }
end

-- multipoint样式示例
local function MULTIPOINT_STYLE()
  return {
    ['2d'] = {
      style = 'multipoint',
      size = 15,
      color = '0xFFCD5E40',
      shape = 'Icon', -- Circle,Square,Sphere,Cylinder,Cubiod,Icon
      rotate = 45,
      height = 45,
      length = 3,
      width = 3,
      icon = 'icons/XXX.png', -- 仅当shape为“Icon”时有效
    },
  }
end

-----------------------------------
-- 3D样式参考设置
-----------------------------------
local function DEFAULT_STYLE_3D()
  local height_3D = 4 -- 3D多边形的高度
  -- 2D与3D面和线相关属性保持一致，确保样式美观
  local face_color = '0XFFFF7F27'
  local outline_color = '0XFF7092BE'
  local outline_width = 0.05
  return {
    -- 2D样式配置
    ['2d'] = {
      style = 'polygon',
      face = {
        color = face_color,
        enable_alpha = false,
        texture = nil,
        automatic_scale = nil
      },
      outline = {
        color = outline_color,
        width = outline_width,
        enable_alpha = true,
		    alignment = 'AlignRight',
        enable_width = true,--具备线宽的绘制方式
      },
      left_side = {}
    },
    -- 3D样式配置
    ['3d'] = {
      style = 'polygon', -- 3D样式多边形，由3D样式的边和一个拔高的面组成
      face_on_bottom = false, -- 为false时 height才有效
      height = height_3D, -- 多边形面高度，一般要和outline的高度相同

      face = {
        color = face_color,
        enable_alpha = false,
      },

      -- 3D效果的多边形边由三个面组成
      outline = {
        color = outline_color, -- face边界颜色
        width = outline_width, -- face边界宽度，也是top_side的宽度
        height = height_3D, -- 3D多边形边高度
        enable_alpha = true,
        enable_edge_shadow = true, -- 3D侧边是否进行阴影处理，默认开启
		    alignment = 'AlignRight',
        enable_width = true,--具备线宽的绘制方式
      }
    }
  }
end

local function DEFAULT_STYLE_3D_2()
  local height_3D = 2.5 -- 3D多边形的高度
  -- 2D与3D面和线相关属性保持一致，确保样式美观
  local face_color = '0xFFFFAEEB'
  local outline_color = '0XFF7092BE'
  local outline_width = 0.05
  return {
    ['2d'] = {
      style = 'polygon',
      face = {
        color = face_color,
        enable_alpha = false,
        texture = null,
        automatic_scale = null
      },
      outline = {
        color = outline_color,
        width = outline_width,
        enable_alpha = false,
		alignment = 'AlignRight',
        enable_width = true,--具备线宽的绘制方式
      },
      left_side = {}
    },
    ['3d'] = {
      style = 'polygon',
      face_on_bottom = false, --为false时 height才有效
      height = height_3D, --如果多边形有面的话，要和outline的高度相同

      face = {
        color = face_color,
        enable_alpha = false,
      },
      outline = {
        color = outline_color,
        width = outline_width,
        height = height_3D,
        enable_alpha = false,
        -- enable_edge_shadow = false
		    alignment = 'AlignRight',
        enable_width = true,--具备线宽的绘制方式
      }
    }
  }
end

CONFIG = {
  views = {
    default = {
      --back_color = '0xffabcdef', --设置初始化mapView背景颜色（16进制ARGB），如果设置back_image，此属性将无效
      --back_image = "", --设置初始化mapView背景图片路径
      layers = {
        Frame = {
          --对应着名字为Frame的图层
          height_offset = 0.2, --图层的高度，用于错层显示
          renderer = {
            type = 'simple', --图层对应的renderer的类型，simple表示这个图层只存在一种样式，所有加入到这个图层的Feature自动启用下面配置的样式
            ['2d'] = {
              --2d的样式
              style = 'polygon', --对应一个样式的类型，假设这是一个正方形
              face = {
                color = '0xff00ff00', --正方形的面颜色
                enable_alpha = false --是否开启alpha通道
              },
              outline = {
                color = '0xff000000', --正方形外边框的颜色
				        alignment = 'AlignLeft', -- 多边形外框线对齐方式设置, 取值为:'AlignLeft'、'AlignCenter'、'AlignRight',沿顺时针方向分别表示居左(外)、居中、居右(内)对齐
                width = 1, --正方形外边框粗细
                enable_width = true,--具备线宽的绘制方式
              },
            },
            ['3d'] = {
              style = 'polygon',
              face_on_bottom = false, --为false时 height才有效
              height = 4, --如果多边形有面的话，要和outline的高度相同
              face = {
                color = '0XFFFF7F27',
                enable_alpha = false
              },
              outline = {
                color = '0XFF000000',
                width = 0,
                height = 4,
				        alignment = 'AlignLeft', -- 多边形外框线对齐方式设置, 取值为:'AlignLeft'、'AlignCenter'、'AlignRight',沿顺时针方向分别表示居左(外)、居中、居右(内)对齐
                enable_width = true,--具备线宽的绘制方式
              },
            },
          }
        },
        Area = {
          --对应着名字为Area的图层
          height_offset = 0.1,
          renderer = {
            type = 'unique', --这里提供了另外一种renderder，unique表示这个图层可以根据一些字段去匹配，不同的的匹配规则套用不同的样式
            key = {
              "id", 'categoryList.categoryId' --可以用多个字段去匹配，基本规则是如果匹配到了id，那后面的就不再匹配了。categoryList.categoryId表示匹配categoryList下的categoryId,代表着层级关系
            },
            -- key = "id",  --同样，支持单个字段的匹配模式
            default = DEFAULT_STYLE(), --这里是调用了上面申明的一个函数，我们可以自定义各种函数来简化style的定义
            styles = {
              [1995] = {
                --假设这里匹配了到id为1995的Feature，那么就使用下面设置的样式
                ['2d'] = {
                  style = 'polygon', --同样是一个polygon样式
                  face = {
                    color = '0xF0ff00ff',
                    enable_alpha = true,
                  },
                  outline = {
                    color = '0xff000000',
                    width = 0.3,
					          alignment = 'AlignRight',
                    enable_width = true,--具备线宽的绘制方式
                  },
                }
              },
              [4018000000] = {
                --假设匹配到了category为4018000000的Feature，那么使用下面设置的样式
                ['2d'] = {
                  style = 'polygon',
                  face = {
                    color = '0xF0ff0000',
                    enable_alpha = true,
                  },
                  outline = {
                    color = '0xff000000',
                    width = 0.3,
					          alignment = 'AlignRight',
                    enable_width = true,--具备线宽的绘制方式
                  },
                }
              },
              -- 假设匹配到了category为4018000001的Feature,不渲染
              [4018000001] = NULLSTYLE(),
              -- 正则表达式支持，下面的例子是匹配所有40开头，7个数字结尾的
              ['40\\d{7}'] = NULLSTYLE()
            },
            updatestyles = {
              [1] = DEFAULT_STYLE_3D(),
              [2] = DEFAULT_STYLE_3D_2(),
            },
          }
        },
        -- 不带图标的文字层样式配置示例
        Area_text = {
          --对应着名字为Area_text的图层,这是一个系统默认的图层，如果你在请求的图层中请求了shop，则会自动生成这个图层，用来显示文字信息
          collision_detection = true,
          -- font_path = "C:\\Windows\\Fonts\\simhei.ttf", --windows  --需要提供字体文件
          font_path = "/system/fonts/DroidSansFallback.ttf", --android
          renderer = {
            type = 'simple', --是一个simple的renderer
            ['2d'] = {
              style = 'annotation', --一个文字显示的样式
              color = '0xFFFFFFFF', --文字颜色
              field = 'name', --提取Feature中的那个字段对应的文字作为显示内容
              --field = '#name##display#', --如果要匹配多个字段，可以使用这种方法
              size = 4, --文字大小
              height = 0.0, -- 如果开启3D状态，需设置此项，数值参考3D的height
              outline_color = '0xFF000000', --外轮廓颜色
              outline_width = 1, --外轮廓宽度
              anchor_x = 0.5, --锚点x
              anchor_y = 0.5, --锚点y，（0 ，0）为左上角，（1，1）为右下角
              aabbox_extend = 2, --外包盒扩大像素数，用于扩大碰撞检测范围
              enable_fadein = true, --是否开启渐显动画效果，默认开启
            },
          }
        },
        -- 带图标的文字层样式配置示例
        Area_text = {
          collision_detection = true,
          font_path = GET_FONT_PATH(),
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'annotation', --一个文字显示的样式
              color = '0xFFFFFFFF', --文字颜色
              field = 'name', --提取Feature中的那个字段对应的文字作为显示内容
              --field = '#name##display#', --如果要匹配多个字段，可以使用这种方法
              size = 6, --文字大小
              height = 0.0, -- 如果开启3D状态，需设置此项，数值参考3D的height
              outline_color = '0xFF000000', --外轮廓颜色
              outline_width = 1, --外轮廓宽度
              anchor_x = 0.5, --锚点x
              anchor_y = 0.5, --锚点y，（0 ，0）为左上角，（1，1）为右下角
              aabbox_extend = 2, --外包盒扩大像素数，用于扩大碰撞检测范围
              enable_fadein = true, --是否开启渐显动画效果，默认开启
              -- 锚点图标样式配置
              anchor_style = {
                style = 'icon',
                -- icon = "/system/icon/demo.png", -- 只要配置了当前属性，就加载本地图片,则"icon_url"、"icon_cache"、"icon_online"三个属性无效
                icon_url = 'http://api.ipalmap.com/logo/64/', -- 设置服务器图标下载地址，线上提供32x32、64x64、128x128、256x256分辨率的图标，对应把后面数字改为32、64、128、256即可
                icon_cache = GET_CACHE_PATH() .. "/icon/64/", -- 设置图标缓存地址、根据实际开发需求配置
                icon_online = 'logo', -- 使用哪个字段的名称作为图标资源名称
                use_texture_origin_size = true, -- 是否按照原始尺寸(px)显示图标，如果为true，则下面宽高无效
                unit = 'pt', -- 图标大小(width、height)使用的单位,"px"表示像素,"pt"表示1/72英寸
                width = 6,
                height = 6,
              },

            },
          }
        },
        -- 公共设施层样式配置示例
        Facility = {
        -- height_offset = -1,
        collision_detection = true,
        height_offset = -0.2,
        renderer = {
          type = 'unique',
          key = {
            'id',
            'category'
          },
          default = {
            ['2d'] = {
              style = 'icon',
              -- icon = "/system/icon/demo.png", -- 只要配置了当前属性，就加载本地图片,则"icon_url"、"icon_cache"、"icon_online"三个属性无效
              icon_url = 'http://api.ipalmap.com/logo/64/', -- 设置服务器图标下载地址，线上提供32x32、64x64、128x128、256x256分辨率的图标，对应把后面数字改为32、64、128、256即可
              icon_cache = GET_CACHE_PATH() .. "/icon/64/", -- 设置图标缓存地址、根据实际开发需求配置
              icon_online = 'logo', -- 使用哪个字段的名称作为图标资源名称
              anchor_x = 0.5,
              anchor_y = 0.5,
              use_texture_origin_size = false,
              unit = 'pt', -- 图标大小(width、height)使用的单位,"px"表示像素,"pt"表示1/72英寸
              width = 6,
              height = 6,
              enable_fadein = false,
            },
          },
          styles = {

          },

          updatestyles = {

          },
        },
       },
        positioning = {
          --对应着名字为positioning的图层,这个可以对应着自己新建的一个图层，比如你新建了一个定位图层，name为positioning，这样就会读取这个配置信息
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'icon',
              icon = "icons/location.png",
              use_texture_origin_size = false,
              width = 48,
              height = 79,
            },
            ['3d'] = {
              style = 'icon',
              icon = "icons/location2.png",
              -- top_edge_width与edge_height设置比例与icon实际比例一致
              top_edge_width = 3.2,
              bottom_edge_width = 3.2, -- 与top_edge_width一致
              edge_height = 5.3,
              height = 4.4, -- 3D贴图与地面的高度
            }
          }
        },
        icon_layer = {
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'icon', --显示图标
              icon = 'icons/1001.png', --图标的文件名，需要防止在assets下
              anchor_x = 0.5, --锚点x
              anchor_y = 0.5 --锚点y，（0 ，0）为左上角，（1，1）为右下角
            },
          }
        },
        navigate = {
          -- 导航图层参考样式设置
          height_offset = -0.2,
          renderer = {
            type = 'simple',
            ['2d'] = {
              style = 'linestring',
              color = '0xFF006699', -- 颜色
              width = 0.5, -- 线宽
              line_style = 'NONE', -- 线型，NONE、ARROW、DASHED
              has_arrow = true, -- 是否绘制方向指示箭头，仅在line_style为NONE时有效
              has_start = true, -- 绘制起始点
              has_end = true, -- 绘制终点
              automatic_scale = true, -- 导航线自适应地图大小
              enable_width = true,--具备线宽的绘制方式
            },
          }
        },
        navigate = {
          height_offset = -0.3,
          renderer = {
            type = 'unique',
            key = {
              'navi_name', -- 经停点默认使用这个字段区别导航线和经停点
            },
            default = {
              ['2d'] = {
                style = 'linestring',
                color = '0xFF006699', -- 颜色
                width = 0.5, -- 线宽
                line_style = 'NONE', -- 线型，NONE、ARROW、DASHED
                has_arrow = true, -- 是否绘制方向指示箭头，仅在line_style为NONE时有效
                has_start = true, -- 绘制起始点
                has_end = true, -- 绘制终点
                automatic_scale = true, -- 导航线自适应地图大小
              },
            },
            styles = {
              ["transit"] = MULTIPOINT_STYLE(), -- 这个是固定匹配经停点的属性
            }
          }
        },
      }
    }
  }
}
