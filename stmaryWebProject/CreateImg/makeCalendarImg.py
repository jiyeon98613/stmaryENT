import json
import calendar
from PIL import Image, ImageDraw, ImageFont
import datetime


#read json file
cur_date = datetime.datetime.now()
cur_y = cur_date.year
cur_m = cur_date.month
with open(str(cur_y) + '_holidays.json', 'r') as file:
    data = json.load(file)
def create_calendar(year, month, holidays):
    #create cal
    cal = calendar.monthcalendar(year, month)

    #create image
    img = Image.new('RGBA', (350, 250), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    font = ImageFont.load_default()

    #font size
    font_path = "C:\\Windows\\Fonts\\arial.ttf"
    small_font = ImageFont.truetype("arial.ttf", 6)

    #draw cal
    x = 10
    y = 10
    day_height = 45
    
    for week in cal:
        for day in week:
            color = 'black'
            day_text = str(day) if day != 0 else ''
            holiday_text = ''   
            
            #holidays and sundays
            if day != 0:
                if str(day) in holidays:
                    holiday_text = holidays[str(day)]
                    color = 'red'
                elif datetime.date(year, month, day).weekday() == 6:
                    color = 'red'
            
            # draw dates
            draw.text((x, y), day_text, fill=color, font=font)

            # draw title
            if holiday_text:
                draw.text((x, y + 20), holiday_text, fill='red', font=font)

            x += 50
        y += day_height
        x = 10

        
    img.save(f'calendar.png')


for month_name, month_data in data.items():
    month_number = datetime.datetime.strptime(month_name, '%B').month
    create_calendar(2023, month_number, month_data['holidays'])